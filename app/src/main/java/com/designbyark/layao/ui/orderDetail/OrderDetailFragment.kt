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
import com.designbyark.layao.R
import com.designbyark.layao.adapters.OrderCartAdapter
import com.designbyark.layao.common.*
import com.designbyark.layao.data.Order
import com.designbyark.layao.databinding.FragmentOrderDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class OrderDetailFragment : Fragment() {

    private var firebaseUser: FirebaseUser? = null
    private var orderId: String? = null
    private var orderDocument: DocumentReference? = null

    private lateinit var collectionUserReference: CollectionReference
    private lateinit var binding: FragmentOrderDetailBinding

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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_order_detail, container, false)
        binding.detail = this
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

        orderDocument = orderId?.let { collectionReference.document(it) }
        getOrderData(view)
    }

    fun cancelOrder() {
        orderId?.let {
            showCancelAlert(requireContext())
        }
    }

    private fun getOrderData(view: View) {
        orderDocument?.addSnapshotListener { snapshot, exception ->

            if (exception != null) {
                Log.w(LOG_TAG, "Listen failed.", exception)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val model = snapshot.toObject(Order::class.java)
                if (model != null) {
                    orderStatus = model.orderStatus
                    binding.mOrderId.text = String.format(
                        "Order ID: %s",
                        formatOrderId(model.orderId, model.contactNumber)
                    )
                    setOrderStatus(model.orderStatus, view.context)
                    binding.mCustomerName.text = String.format("Placed by %s", model.fullName)
                    binding.mOrderAddress.text =
                        String.format("Delivery at %s", model.completeAddress)
                    binding.mCustomerContact.text =
                        String.format("Contact: %s", model.contactNumber)
                    binding.mCartTotal.text =
                        String.format(
                            Locale.getDefault(),
                            "Grand Total: Rs. %.2f",
                            model.grandTotal
                        )
                    binding.mTotalItems.text =
                        String.format(
                            Locale.getDefault(),
                            "Total items: %d items",
                            model.totalItems
                        )
                    binding.mOrderTiming.text = String.format(
                        "Order placed on %s",
                        formatDate(model.orderTime.toDate())
                    )

                    val orderCartAdapter = OrderCartAdapter(model.items)
                    binding.mOrderCartRV.adapter = orderCartAdapter
                }
            } else {
                Log.d(LOG_TAG, "Current data: null")
            }
        }
    }

    private fun setStatusUI(@ColorInt strokeColor: Int, visibility: Int) {
        binding.orderStatusSection.strokeColor = strokeColor
        binding.mCancelOrder.visibility = visibility
    }

    private fun setOrderStatus(status: Int, context: Context) {
        binding.mOrderStatus.text = getOrderStatus(status)
        when (status) {
            0 -> setStatusUI(
                getColor(context, android.R.color.holo_orange_dark),
                View.VISIBLE
            )
            1 -> setStatusUI(
                getColor(context, android.R.color.holo_blue_dark),
                View.VISIBLE
            )
            2 -> setStatusUI(
                getColor(context, android.R.color.holo_green_dark),
                View.INVISIBLE
            )
            3 -> setStatusUI(
                getColor(context, android.R.color.holo_purple),
                View.INVISIBLE
            )
            4 -> setStatusUI(
                getColor(context, android.R.color.holo_red_dark),
                View.INVISIBLE
            )

            5 -> setStatusUI(
                getColor(context, android.R.color.holo_green_dark),
                View.INVISIBLE
            )
            6 -> setStatusUI(
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
