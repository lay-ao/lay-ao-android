package com.designbyark.layao.ui.cart

import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.data.cart.Cart
import com.designbyark.layao.databinding.BodyCartItemBinding

class CartViewHolder(private val binding: BodyCartItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    val deleteItem = binding.deleteItem
    val decreaseQuantity = binding.decreaseQuantity
    val increaseQuantity = binding.increaseQuantity

    fun bind (cart: Cart) {
        binding.cart = cart
        binding.executePendingBindings()
    }
}