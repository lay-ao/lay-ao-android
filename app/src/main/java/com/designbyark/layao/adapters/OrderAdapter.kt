package com.designbyark.layao.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.data.Order
import com.designbyark.layao.databinding.BodyActiveOrderBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class OrderAdapter internal constructor(
    options: FirestoreRecyclerOptions<Order>,
    private val itemClickListener: OrderItemClickListener
) : FirestoreRecyclerAdapter<Order, OrderAdapter.OrderViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BodyActiveOrderBinding.inflate(layoutInflater, parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int, model: Order) {
        holder.bind(model)
        val id = snapshots.getSnapshot(holder.adapterPosition).id
        holder.itemView.setOnClickListener {
            itemClickListener.orderItemClickListener(id)
        }
    }

    interface OrderItemClickListener {
        fun orderItemClickListener(orderId: String)
    }

    inner class OrderViewHolder internal constructor(private val binding: BodyActiveOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.order = order
            binding.executePendingBindings()
        }

    }

}