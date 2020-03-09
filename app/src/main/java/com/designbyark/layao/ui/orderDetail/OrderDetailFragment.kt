package com.designbyark.layao.ui.orderDetail


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.designbyark.layao.R
import com.designbyark.layao.common.*
import com.designbyark.layao.data.Order
import com.designbyark.layao.data.User
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
    private lateinit var navController: NavController

    private var walletAmount: Double = 0.0

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

        val firebaseFirestore = FirebaseFirestore.getInstance()
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser
        val collectionReference = firebaseFirestore.collection(ORDERS_COLLECTION)
        collectionUserReference = firebaseFirestore.collection(USERS_COLLECTION)

        if (firebaseUser != null) {
            collectionUserReference.document(firebaseUser!!.uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    val user = documentSnapshot.toObject(User::class.java)
                    if (user != null) {
                        walletAmount = user.wallet
                    }
                }
        }

        orderDocument = orderId?.let { collectionReference.document(it) }
        // val title = orderId?.take(5)?.toUpperCase(Locale.getDefault())


        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )

        getOrderData(view, requireContext())
        view.mCancelOrder.setOnClickListener {
            orderId?.let {
                showCancelAlert(requireContext())
                view.mCancelOrder.visibility = View.INVISIBLE
            }
        }
    }

    private fun getOrderData(view: View, context: Context) {
        orderDocument?.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(LOG_TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val model = snapshot.toObject(Order::class.java)
                if (model != null) {
                    view.mOrderId.text = String.format(
                        "Order ID: %s",
                        formatOrderId(model.orderId, model.contactNumber)
                    )
                    setOrderStatus(view, model.orderStatus, context)
                    view.mCustomerName.text = String.format("Order placed by %s", model.fullName)
                    view.mOrderAddress.text = String.format("Delivery at %s", model.completeAddress)
                    view.mCustomerContact.text = String.format("Contact: %s", model.contactNumber)
                    view.mGrandTotal.text =
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
            0 -> setTextColor(
                view,
                android.R.color.holo_orange_dark,
                view.mCancelOrder,
                View.VISIBLE,
                context
            )
            1 -> setTextColor(
                view,
                android.R.color.holo_green_dark,
                view.mCancelOrder,
                View.VISIBLE,
                context
            )
            2 -> setTextColor(
                view,
                android.R.color.holo_blue_dark,
                view.mCancelOrder,
                View.INVISIBLE,
                context
            )
            3 -> setTextColor(
                view,
                android.R.color.holo_purple,
                view.mCancelOrder,
                View.INVISIBLE,
                context
            )
            4 -> setTextColor(
                view,
                android.R.color.holo_red_light,
                view.mCancelOrder,
                View.VISIBLE,
                context
            )
            5 -> setTextColor(
                view,
                android.R.color.holo_green_dark,
                view.mCancelOrder,
                View.INVISIBLE,
                context
            )
            6 -> setTextColor(
                view,
                android.R.color.holo_red_dark,
                view.mCancelOrder,
                View.INVISIBLE,
                context
            )
            else -> setTextColor(
                view,
                android.R.color.black,
                view.mCancelOrder,
                View.VISIBLE,
                requireContext()
            )
        }
    }

    private fun setTextColor(
        view: View,
        @ColorRes color: Int,
        button: Button,
        visibility: Int,
        context: Context
    ) {
        view.mOrderStatus.setTextColor(
            ContextCompat.getColor(
                context,
                color
            )
        )
        button.visibility = visibility
    }

    private fun showCancelAlert(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Warning")
            .setIcon(R.drawable.ic_warning_color_24dp)
            .setMessage(
                "Cancelling the order will charge you an amount of " +
                        "Rs. 30 which will be added to your wallet"
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
        collectionUserReference.document(firebaseUser!!.uid).update("wallet", walletAmount + 30)
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.isComplete) {
                    orderDocument?.update("orderStatus", 6)
                    displayNotification(
                        context,
                        R.drawable.ic_favorite_red,
                        "Order cancelled",
                        "Your wallet has been charged with Rs. 30 for cancelling the order"
                    )
                    dialog.dismiss()
                } else {
                    Log.e(LOG_TAG, "update wallet fine: ${task.exception?.localizedMessage}")
                    return@addOnCompleteListener
                }
            }
            .addOnFailureListener { exception ->
                Log.e(LOG_TAG, "update wallet fine: $exception", exception)
                return@addOnFailureListener
            }
    }


}
