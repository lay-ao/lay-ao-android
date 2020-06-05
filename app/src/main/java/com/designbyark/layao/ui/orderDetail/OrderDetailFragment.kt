package com.designbyark.layao.ui.orderDetail


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.designbyark.layao.R
import com.designbyark.layao.adapters.OrderCartAdapter
import com.designbyark.layao.databinding.FragmentOrderDetailBinding
import com.designbyark.layao.util.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import org.joda.time.LocalDate

class OrderDetailFragment : Fragment() {

    private var firebaseUser: FirebaseUser? = null
    private var orderDocument: DocumentReference? = null

    private lateinit var collectionUserReference: CollectionReference
    private lateinit var binding: FragmentOrderDetailBinding

    private var orderStatus: Long = 0
    private val args: OrderDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_order_detail, container, false)
        binding.detail = this
        binding.order = args.order
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        val firebaseFirestore = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        val collectionReference = firebaseFirestore.collection(ORDERS_COLLECTION)

        firebaseUser = firebaseAuth.currentUser
        collectionUserReference = firebaseFirestore.collection(USERS_COLLECTION)

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        }

        orderDocument = args.order.orderId.let { collectionReference.document(it) }

        val orderCartAdapter = OrderCartAdapter(args.order.items)
        binding.mOrderCartRV.adapter = orderCartAdapter

        listenToOrderStatus(view)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
    }

}
