package com.designbyark.layao


import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.common.*
import com.designbyark.layao.data.Order
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class OrderDetailFragment : Fragment() {

    private var orderId: String? = null
    private var orderDocument: DocumentReference? = null
    private lateinit var navController: NavController

    private lateinit var orderIdView: TextView
    private lateinit var statusView: TextView
    private lateinit var nameView: TextView
    private lateinit var addressView: TextView
    private lateinit var contactView: TextView
    private lateinit var grandTotalView: TextView
    private lateinit var totalItemsView: TextView
    private lateinit var timeView: TextView

    private lateinit var cancelOrder: Button

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            orderId = it.getString("orderId")
        }

        val firebase = FirebaseFirestore.getInstance()
        val collectionReference = firebase.collection(ORDERS_COLLECTION)
        orderDocument = orderId?.let { collectionReference.document(it) }
        val title = orderId?.take(5)?.toUpperCase(Locale.getDefault())

        (requireActivity() as AppCompatActivity).run {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setTitle("Order #$title")
        }
        setHasOptionsMenu(true)

        navController = Navigation.findNavController(
            requireActivity(),
            R.id.nav_host_fragment
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_order_detail, container, false)
        findingViews(root)
        getOrderData()
        cancelOrder.setOnClickListener {
            orderId?.let {
                showCancelAlert(requireContext())
                cancelOrder.visibility = View.INVISIBLE
            }
        }
        return root
    }

    private fun getOrderData() {
        orderDocument?.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(LOG_TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val model = snapshot.toObject(Order::class.java)
                if (model != null) {
                    orderIdView.text = String.format(
                        "Order ID: %s",
                        formatOrderId(model.orderId, model.contactNumber)
                    )
                    setOrderStatus(model.orderStatus)
                    nameView.text = String.format("Order placed by %s", model.fullName)
                    addressView.text = String.format("Delivery at %s", model.completeAddress)
                    contactView.text = String.format("Contact: %s", model.contactNumber)
                    grandTotalView.text =
                        String.format(
                            Locale.getDefault(),
                            "Grand Total: Rs. %.0f",
                            model.grandTotal
                        )
                    totalItemsView.text =
                        String.format(
                            Locale.getDefault(),
                            "Total items: %d items",
                            model.totalItems
                        )
                    timeView.text = String.format(
                        "Order placed on %s",
                        formatTimeDate(model.orderTime.toDate())
                    )

                    val orderCartAdapter = OrderCartAdapter(requireContext(), model.items)
                    recyclerView.adapter = orderCartAdapter

                }

            } else {
                Log.d(LOG_TAG, "Current data: null")
            }
        }
    }

    private fun findingViews(root: View) {
        root.run {
            orderIdView = findViewById(R.id.order_id)
            statusView = findViewById(R.id.order_status)
            nameView = findViewById(R.id.customer_name)
            addressView = findViewById(R.id.order_address)
            contactView = findViewById(R.id.customer_contact)
            grandTotalView = findViewById(R.id.grand_total)
            totalItemsView = findViewById(R.id.total_items)
            timeView = findViewById(R.id.order_timing)
            cancelOrder = findViewById(R.id.cancel_order)
            recyclerView = findViewById(R.id.order_cart_recycler_view)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> navController.navigateUp()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setOrderStatus(status: Int) {
        statusView.text = getOrderStatus(status)
        when (status) {
            0 -> setTextColor(android.R.color.holo_orange_dark, cancelOrder, View.VISIBLE)
            1 -> setTextColor(android.R.color.holo_green_dark, cancelOrder, View.VISIBLE)
            2 -> setTextColor(android.R.color.holo_blue_dark, cancelOrder, View.INVISIBLE)
            3 -> setTextColor(android.R.color.holo_purple, cancelOrder, View.INVISIBLE)
            4 -> setTextColor(android.R.color.holo_red_light, cancelOrder, View.VISIBLE)
            5 -> setTextColor(android.R.color.holo_green_dark, cancelOrder, View.INVISIBLE)
            6 -> setTextColor(android.R.color.holo_red_dark, cancelOrder, View.INVISIBLE)
            else -> setTextColor(android.R.color.black, cancelOrder, View.VISIBLE)
        }
    }

    private fun setTextColor(@ColorRes color: Int, button: Button, visibility: Int) {
        statusView.setTextColor(
            ContextCompat.getColor(
                requireContext(),
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
                orderDocument?.update("orderStatus", 6)
                displayNotification(
                    context,
                    R.drawable.ic_favorite_color_24dp,
                    "Order cancelled",
                    "Your wallet has been charged with Rs. 30 for cancelling the order"
                )
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}
