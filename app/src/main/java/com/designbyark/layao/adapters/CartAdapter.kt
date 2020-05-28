package com.designbyark.layao.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.data.cart.Cart
import com.designbyark.layao.databinding.BodyCartItemBinding
import com.designbyark.layao.ui.cart.CartViewHolder
import com.designbyark.layao.viewmodels.CartViewModel

class CartAdapter internal constructor(
    private val cartViewModel: CartViewModel
) : RecyclerView.Adapter<CartViewHolder>() {
    private var items = emptyList<Cart>() // Cached copy of items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BodyCartItemBinding.inflate(layoutInflater, parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount() = items.size

    @ExperimentalStdlibApi
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val model = items[position]
        holder.bind(model)
        holder.run {

            deleteItem.setOnClickListener {
                cartViewModel.deleteCartItem(model)
                notifyDataSetChanged()
            }

            increaseQuantity.setOnClickListener {
                if (model.quantity != model.stock) {
                    if (model.discount > 0) {
                        model.quantity++
                        model.total = (model.price - (model.price * (model.discount / 100.0))) * model.quantity
                        cartViewModel.updateCart(model)
                    } else {
                        model.quantity++
                        model.total = model.price* model.quantity
                        cartViewModel.updateCart(model)
                    }
                }
                notifyDataSetChanged()
            }

            decreaseQuantity.setOnClickListener {
                val limit: Long = 1
                if (model.quantity != limit) {
                    if (model.discount > 0) {
                        model.quantity--
                        model.total = (model.price - (model.price * (model.discount / 100.0))) * model.quantity
                        cartViewModel.updateCart(model)
                    } else {
                        model.quantity--
                        model.total = model.price* model.quantity
                        cartViewModel.updateCart(model)
                    }
                }
                notifyDataSetChanged()
            }
        }
    }

    internal fun setItems(items: List<Cart>) {
        this.items = items
        notifyDataSetChanged()
    }
}