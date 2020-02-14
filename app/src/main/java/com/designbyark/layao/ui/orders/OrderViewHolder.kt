package com.designbyark.layao.ui.orders

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.common.formatDate
import com.designbyark.layao.common.formatOrderId
import com.designbyark.layao.common.formatTime
import com.designbyark.layao.common.getOrderStatus
import com.google.firebase.Timestamp
import java.util.*

class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    internal fun setOrderStatus(status: Int, context: Context, @ColorRes color: Int) {
        val textView: TextView = itemView.findViewById(R.id.order_id)
        textView.text = getOrderStatus(status)
        textView.setTextColor(ContextCompat.getColor(context, color))
    }

    internal fun setOrderId(orderId: String, phoneNumber: String) {
        val textView: TextView = itemView.findViewById(R.id.order_status)
        textView.text = formatOrderId(orderId, phoneNumber)
    }

    internal fun setCustomerName(fullName: String) {
        val textView: TextView = itemView.findViewById(R.id.customer_name)
        textView.text = String.format("Order placed by %s", fullName)
    }

    internal fun setAddress(address: String) {
        val textView: TextView = itemView.findViewById(R.id.order_address)
        textView.text = String.format("Delivery at %s", address)
    }

    internal fun setCustomerContact(contact: String) {
        val textView: TextView = itemView.findViewById(R.id.customer_contact)
        textView.text = String.format("Contact: %s", contact)
    }

    internal fun setGrandTotal(grandTotal: Double) {
        val textView: TextView = itemView.findViewById(R.id.grand_total)
        textView.text = String.format(Locale.getDefault(), "Rs. %.0f", grandTotal)
    }

    internal fun setTotalItems(itemCount: Int) {
        val textView: TextView = itemView.findViewById(R.id.total_items)
        textView.text = String.format(Locale.getDefault(), "%d items", itemCount)
    }

    internal fun setOrderTime(time: Timestamp) {
        val textView: TextView = itemView.findViewById(R.id.total_items)
        textView.text = String.format(
            "Order placed on %s at %s",
            formatDate(time.toDate()),
            formatTime(time.toDate())
        )
    }

}