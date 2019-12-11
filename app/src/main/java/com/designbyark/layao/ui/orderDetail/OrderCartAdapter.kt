package com.designbyark.layao.ui.orderDetail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.data.cart.Cart
import com.designbyark.layao.ui.cart.CartViewHolder

class OrderCartAdapter internal constructor(
    private val context: Context,
    private var items: List<Cart>
) : RecyclerView.Adapter<CartViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

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
            setPerPrice(model.price, model.unit, model.discount)
            setQuantity(model.price, model.unit, model.quantity, model.discount)
            setBrand(model.brand)
            setDiscount(model.discount)
            setTotal(model.total)
            deleteItem.visibility = View.GONE
        }
    }

}