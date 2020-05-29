package com.designbyark.layao.ui.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.designbyark.layao.R
import com.designbyark.layao.data.Order
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class OrderAdapter internal constructor(
    options: FirestoreRecyclerOptions<Order>,
    private val itemClickListener: OrderItemClickListener
) : FirestoreRecyclerAdapter<Order, OrderViewHolder>(options) {

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.body_active_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int, model: Order) {
        val id = snapshots.getSnapshot(holder.adapterPosition).id
        val status = model.orderStatus
        val context = holder.itemView.context
        holder.run {
            when (status) {
                0 -> setOrderStatus(status, context, android.R.color.holo_orange_dark)
                1 -> setOrderStatus(status, context, android.R.color.holo_green_dark)
                2 -> setOrderStatus(status, context, android.R.color.holo_blue_dark)
                3 -> setOrderStatus(status, context, android.R.color.holo_purple)
                4 -> setOrderStatus(status, context, android.R.color.holo_red_light)
                5 -> setOrderStatus(status, context, android.R.color.holo_green_dark)
                6 -> setOrderStatus(status, context, android.R.color.holo_red_dark)
                else -> setOrderStatus(status, context, android.R.color.black)
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