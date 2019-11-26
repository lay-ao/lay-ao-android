package com.designbyark.layao.ui.cart

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.data.cart.Cart

class CartAdapter internal constructor(
    private val context: Context
) : RecyclerView.Adapter<CartViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var items = emptyList<Cart>() // Cached copy of items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val itemView = inflater.inflate(R.layout.body_cart_item, parent, false)
        return CartViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val model = items[position]
        holder.run {
            setImage(model.image, context)
            setTitle(model.title)
            setUnitLabel(model.unit)
            setPerPrice(model.price, model.unit)
            setQuantity(model.price, model.unit, model.quantity)
            setBrand(model.brand)
            setDiscount(model.discount)
        }
    }

    internal fun setItems(items: List<Cart>) {
        this.items = items
        notifyDataSetChanged()
    }


}