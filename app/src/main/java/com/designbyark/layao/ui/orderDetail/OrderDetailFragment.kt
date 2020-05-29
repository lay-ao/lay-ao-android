package com.designbyark.layao.ui.orderDetail


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import com.designbyark.layao.R
import com.designbyark.layao.common.*
import com.designbyark.layao.data.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_order_detail.view.*
import java.util.*

class OrderDetailFragment : Fragment() {

    private var firebaseUser: FirebaseUser? = null
    private var orderId: String? = null
    private var orderDocument: DocumentReference? = null

    private lateinit var collectionUserReference: CollectionReference

    private var walletAmount: Double = 0.00
    private var fineCount: Long = 0
    private var orderStatus: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            orderId = it.getString("orderId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        }

        setCancellingDesc(view)

        val firebaseFirestore = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser
        val collectionReference = firebaseFirestore.collection(ORDERS_COLLECTION)
        collectionUserReference = firebaseFirestore.collection(USERS_COLLECTION)

        if (firebaseUser != null) {
            collectionUserReference.document(firebaseUser!!.uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    walletAmount = documentSnapshot.getDouble("wallet") ?: -1.00
                    fineCount = documentSnapshot.getLong("fineCount") ?: -1
                }
        }

        orderDocument = orderId?.let { collectionReference.document(it) }

        getOrderData(view, requireContext())
        view.mCancelOrder.setOnClickListener {
            orderId?.let {
                showCancelAlert(requireContext())
                view.mCancelOrder.visibility = View.INVISIBLE
            }
        }
    }

    private fun setCancellingDesc(view: View) {
        val cancellingOrder = requireActivity().resources.getString(R.string.order_cancelling_desc)
        val spannableString = SpannableString(cancellingOrder)
        val red = ForegroundColorSpan(Color.RED)
        spannableString.setSpan(red, 32, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        view.mCancelOrderLabel.text = spannableString
    }

    private fun getOrderData(view: View, context: Context) {
        orderDocument?.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.w(LOG_TAG, "Listen failed.", exception)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val model = snapshot.toObject(Order::class.java)
                if (model != null) {
                    orderStatus = model.orderStatus
                    view.mOrderId.text = String.format(
                        "Order ID: %s",
                        formatOrderId(model.orderId, model.contactNumber)
                    )
                    setOrderStatus(view, model.orderStatus, context)
                    view.mCustomerName.text = String.format("Order placed by %s", model.fullName)
                    view.mOrderAddress.text = String.format("Delivery at %s", model.completeAddress)
                    view.mCustomerContact.text = String.format("Contact: %s", model.contactNumber)
                    view.mCartTotal.text =
                        String.format(
                            Locale.getDefault(),
                            "Grand Total: Rs. %.0f",
                            model.grandTotal
                        )
                    view.mTotalItems.text =
                        String.format(
                            Locale.getDefault(),
                            "Total items: %d items",
                            model.totalItems
                        )
                    view.mOrderTiming.text = String.format(
                        "Order placed on %s",
                        formatDate(model.orderTime.toDate())
                    )

                    val orderCartAdapter =
                        OrderCartAdapter(
                            context,
                            model.items
                        )
                    view.mOrderCartRV.adapter = orderCartAdapter

                }

            } else {
                Log.d(LOG_TAG, "Current data: null")
            }
        }
    }

    private fun setOrderStatus(view: View, status: Int, context: Context) {
        view.mOrderStatus.text = getOrderStatus(status)
        when (status) {
            0 -> {
                view.mOrderStatus.setTextColor(getColor(context, android.R.color.holo_orange_dark))
                view.mCancelOrder.visibility = View.VISIBLE
            }
            1 -> {
                view.mOrderStatus.setTextColor(getColor(context, android.R.color.holo_blue_dark))
                view.mCancelOrder.visibility = View.VISIBLE
            }
            2 -> {
                view.mOrderStatus.setTextColor(getColor(context, android.R.color.holo_green_dark))
                view.mCancelOrder.visibility = View.VISIBLE
            }
            3 -> {
                view.mOrderStatus.setTextColor(getColor(context, android.R.color.holo_purple))
                view.mCancelOrder.visibility = View.INVISIBLE
            }
            4 -> {
                view.mOrderStatus.setTextColor(getColor(context, android.R.color.holo_red_dark))
                view.mCancelOrder.visibility = View.VISIBLE
            }
            5 -> {
                view.mOrderStatus.setTextColor(getColor(context, android.R.color.holo_green_dark))
                view.mCancelOrder.visibility = View.INVISIBLE
            }
            6 -> {
                view.mOrderStatus.setTextColor(getColor(context, android.R.color.holo_red_dark))
                view.mCancelOrder.visibility = View.INVISIBLE
            }
            else -> {
                view.mOrderStatus.setTextColor(getColor(context, android.R.color.black))
                view.mCancelOrder.visibility = View.VISIBLE
            }
        }
    }

    private fun showCancelAlert(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Warning")
            .setIcon(R.drawable.ic_warning_color_24dp)
            .setMessage(
                "Cancelling the order will charge you an amount of " +
                        "Rs. 30 if the driver is on the way"
            )
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                cancelOrder(context, dialog)
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun cancelOrder(context: Context, dialog: DialogInterface) {

        if (walletAmount == -1.00) {
            Log.d(LOG_TAG, "Wallet Amount Error")
            return
        }

        if (orderStatus < 2) {
            orderDocument?.update("orderStatus", 6)
            orderDocument?.update("cancelled", true)
            displayNotification(
                context, R.drawable.ic_favorite_red, "Order cancelled",
                "We are sad to see you cancel, we hope you use our services again."
            )
            dialog.dismiss()
            return
        }

        if (orderStatus >= 2) {
            val totalWallet = walletAmount - 30.00
            fineCount++
            firebaseUser?.uid?.let { userId ->
                collectionUserReference.document(userId).update("wallet", totalWallet)
                collectionUserReference.document(userId).update("fineCount", fineCount)
            }
            orderDocument?.update("orderStatus", 6)
            orderDocument?.update("cancelled", true)
            displayNotification(
                context, R.drawable.ic_favorite_red, "Order cancelled",
                "Your wallet has been charged with Rs. 30 for cancelling the order"
            )
            dialog.dismiss()
            return
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

}
