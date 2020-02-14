package com.designbyark.layao.ui.orders

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.designbyark.layao.R
import com.designbyark.layao.common.displayNotification
import com.designbyark.layao.data.Order
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference

class OrderAdapter internal constructor(
    options: FirestoreRecyclerOptions<Order>,
    private val context: Context,
    private val itemClickListener: OrderItemClickListener
) : FirestoreRecyclerAdapter<Order, OrderViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.body_active_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int, model: Order) {
        val id = snapshots.getSnapshot(holder.adapterPosition).id

        holder.run {
            when (model.orderStatus) {
                0 -> setOrderStatus(model.orderStatus, context, android.R.color.holo_orange_dark)
                1 -> setOrderStatus(model.orderStatus, context, android.R.color.holo_green_dark)
                2 -> setOrderStatus(model.orderStatus, context, android.R.color.holo_blue_dark)
                3 -> setOrderStatus(model.orderStatus, context, android.R.color.holo_purple)
                4 -> setOrderStatus(model.orderStatus, context, android.R.color.holo_red_light)
                5 -> setOrderStatus(model.orderStatus, context, android.R.color.holo_green_dark)
                6 -> setOrderStatus(model.orderStatus, context, android.R.color.holo_red_dark)
                else -> setOrderStatus(model.orderStatus, context, android.R.color.black)
            }
            setOrderId(model.orderId, model.contactNumber)
            setCustomerName(model.fullName)
            setAddress(model.completeAddress)
            setCustomerContact(model.contactNumber)
            setGrandTotal(model.grandTotal)
            setTotalItems(model.totalItems)
            setOrderTime(model.orderTime)

            itemView.setOnClickListener {
                itemClickListener.orderItemClickListener(id)
            }
        }
    }

    interface OrderItemClickListener {
        fun orderItemClickListener(orderId: String)
    }


}