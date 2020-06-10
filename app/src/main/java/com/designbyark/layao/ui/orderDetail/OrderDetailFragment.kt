package com.designbyark.layao.ui.orderDetail


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.designbyark.layao.R
import com.designbyark.layao.adapters.OrderCartAdapter
import com.designbyark.layao.data.Order
import com.designbyark.layao.databinding.FragmentOrderDetailBinding
import com.designbyark.layao.util.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat

class OrderDetailFragment : Fragment() {

    private var firebaseUser: FirebaseUser? = null
    private var orderDocument: DocumentReference? = null

    private lateinit var orderCollection: CollectionReference
    private lateinit var closingTime: LocalTime
    private lateinit var openingTime: LocalTime

    private lateinit var collectionUserReference: CollectionReference
    private lateinit var binding: FragmentOrderDetailBinding

    private var orderStatus: Long = 0
    private val args: OrderDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_order_detail, container, false)
        binding.detail = this
        binding.order = args.order
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firebaseFirestore = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        val collectionReference = firebaseFirestore.collection(ORDERS_COLLECTION)

        firebaseUser = firebaseAuth.currentUser
        collectionUserReference = firebaseFirestore.collection(USERS_COLLECTION)
        orderCollection = firebaseFirestore.collection(ORDERS_COLLECTION)

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        }

        orderDocument = args.order.orderId.let { collectionReference.document(it) }

        val orderCartAdapter = OrderCartAdapter(args.order.items)
        binding.mOrderCartRV.adapter = orderCartAdapter

        listenToOrderStatus(view)

        if (isConnectedToInternet(view.context)) {
            firebaseFirestore.collection("Misc").document("store-timing")
                .addSnapshotListener { snapshot, exception ->

                    if (exception != null) {
                        Log.d(LOG_TAG, exception.localizedMessage, exception)
                        return@addSnapshotListener
                    }

                    if (snapshot == null) {
                        Log.d(LOG_TAG, "Misc: snapshot is null!")
                        return@addSnapshotListener
                    }

                    val openingTimeString = snapshot.getString("opening")
                    val closingTimeString = snapshot.getString("closing")

                    openingTime =
                        LocalTime.parse(openingTimeString, DateTimeFormat.forPattern("hh:mm a"))
                    closingTime =
                        LocalTime.parse(closingTimeString, DateTimeFormat.forPattern("hh:mm a"))

                }
        }
    }

    fun cancelOrder() {
        showCancelAlert(requireContext())
    }

    private fun listenToOrderStatus(view: View) {
        orderDocument?.addSnapshotListener { snapshot, exception ->

            if (exception != null) {
                Log.w(LOG_TAG, "Listen failed: ", exception)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                orderStatus = snapshot.get("orderStatus") as Long
                setOrderStatus(orderStatus, view.context)
            } else {
                Log.w(LOG_TAG, "Current data: null")
            }
        }
    }

    private fun setStatusUI(@ColorInt strokeColor: Int, visibility: Int) {
        binding.orderStatusSection.strokeColor = strokeColor
        binding.mCancelOrder.visibility = visibility
    }

    private fun setOrderStatus(status: Long, context: Context) {
        binding.mOrderStatus.text =
            getOrderStatus(status)
        when (status) {
            -1L -> setStatusUI(
                getColor(context, android.R.color.holo_blue_dark),
                View.VISIBLE
            )
            0L -> setStatusUI(
                getColor(context, android.R.color.holo_orange_dark),
                View.VISIBLE
            )
            1L -> setStatusUI(
                getColor(context, android.R.color.holo_blue_dark),
                View.VISIBLE
            )
            2L -> setStatusUI(
                getColor(context, android.R.color.holo_green_dark),
                View.INVISIBLE
            )
            3L -> setStatusUI(
                getColor(context, android.R.color.holo_purple),
                View.INVISIBLE
            )
            4L -> setStatusUI(
                getColor(context, android.R.color.holo_red_dark),
                View.INVISIBLE
            )

            5L -> setStatusUI(
                getColor(context, android.R.color.holo_green_dark),
                View.INVISIBLE
            )
            6L -> setStatusUI(
                getColor(context, android.R.color.holo_red_dark),
                View.INVISIBLE
            )
            else -> setStatusUI(getColor(context, android.R.color.black), View.VISIBLE)
        }
    }

    private fun showCancelAlert(context: Context) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Warning")
            .setIcon(R.drawable.ic_warning_color_24dp)
            .setMessage("Are you sure you want to cancel the order?")
            .setPositiveButton("Yes") { dialog, _ ->

                if (!isConnectedToInternet(context)) {
                    showNoInternetDialog(context)
                    return@setPositiveButton
                }

                orderDocument?.update("orderStatus", 6)
                orderDocument?.update("cancelled", true)
                displayNotification(
                    context, R.drawable.ic_favorite_red, "Order cancelled",
                    "We are sad to see you cancel, we hope you use our services again."
                )
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showNoInternetDialog(context: Context) {
        MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_no_wifi)
            .setTitle("No network found")
            .setMessage("Kindly connect to a network to cancel the order.")
            .setPositiveButton("Try Again") { dialog, _ ->
                if (isConnectedToInternet(requireContext())) {
                    showCancelAlert(context)
                } else {
                    showNoInternetDialog(context)
                }
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showNoInternetReorderDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_no_wifi)
            .setTitle("No network found")
            .setMessage("Kindly connect to a network to to reorder.")
            .setPositiveButton("Try Again") { dialog, _ ->
                if (isConnectedToInternet(requireContext())) {
                    dialog.dismiss()
                } else {
                    showNoInternetReorderDialog()
                }
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (args.order.orderStatus >= 5L) {
            inflater.inflate(R.menu.order_detail_menu, menu)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {

        requireActivity().invalidateOptionsMenu()
        if (!isConnectedToInternet(requireContext())) {
            menu.findItem(R.id.no_wifi).isVisible = true
        }

        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_reorder -> {
                reorder()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun reorder() {

        if (!isConnectedToInternet(requireContext())) {
            showNoInternetReorderDialog()
            return
        }

        val order = args.order
        order.orderTime = Timestamp.now()
        order.cancelled = false
        order.orderStatus = 0

        val now = LocalTime.now()
        if (now.isAfter(openingTime) && now.isBefore(closingTime) || now.isEqual(openingTime)) {
            displayConfirmationDialog(order, args.order.contactNumber)
        } else {
            displayScheduledOrderDialog(order, args.order.contactNumber)
        }

    }

    private fun displayScheduledOrderDialog(order: Order, phoneNumber: String) {

        val tomorrow = LocalDateTime.now().plusDays(1)
            .withTime(9, 0, 0, 0)

        MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_time)
            .setTitle("Scheduling Order")
            .setMessage(
                "Reorder will be scheduled for tomorrow (${formatDate(
                    tomorrow.toDate()
                )} at ${formatTime(tomorrow.toDate())}). " +
                        "Are you sure you want to continue?"
            )
            .setPositiveButton(android.R.string.ok) { _, _ ->
                order.scheduledTime = Timestamp(tomorrow.toDate())
                order.scheduled = true
                order.orderStatus = -1
                placeOrder(order, phoneNumber)
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun displayConfirmationDialog(order: Order, phoneNumber: String) {

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Confirm Reorder")
            .setMessage("Are you sure you want us to deliver at ${order.completeAddress}")
            .setPositiveButton("Yes") { _, _ ->
                order.orderStatus = 0
                placeOrder(order, phoneNumber)
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun placeOrder(order: Order, phoneNumber: String) {
        orderCollection.add(order)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(requireContext(), "Order Placed!", Toast.LENGTH_LONG).show()
                orderCollection.document(documentReference.id)
                    .update("orderId", documentReference.id)
                displayNotification(
                    requireContext(),
                    R.drawable.ic_favorite_red,
                    "Order Received",
                    "Thank you for placing your order again. Your order id is ${formatOrderId(
                        documentReference.id,
                        phoneNumber.trim()
                    )}. Kindly, contact on our helpline for any further assistance. Thank you."
                )
                Toast.makeText(binding.root.context, "Reorder successful", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e(LOG_TAG, "Error adding document", e)
            }
    }

}
