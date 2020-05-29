package com.designbyark.layao.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.data.cart.Cart
import com.designbyark.layao.databinding.BodyOrderCartItemBinding

class OrderCartAdapter internal constructor(
    private var items: List<Cart>
) : RecyclerView.Adapter<OrderCartAdapter.OrderCartViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderCartViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BodyOrderCartItemBinding.inflate(layoutInflater, parent, false)
        return OrderCartViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: OrderCartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class OrderCartViewHolder(private val binding: BodyOrderCartItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(cart: Cart) {
            binding.cart = cart
            binding.executePendingBindings()
        }
    }

}