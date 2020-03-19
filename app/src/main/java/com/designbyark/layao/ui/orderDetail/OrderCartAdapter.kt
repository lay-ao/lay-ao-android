package com.designbyark.layao.ui.orderDetail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.data.cart.Cart
import com.designbyark.layao.ui.cart.CartViewHolder
import com.designbyark.layao.ui.checkout.OrderCartViewHolder

class OrderCartAdapter internal constructor(
    private val context: Context,
    private var items: List<Cart>
) : RecyclerView.Adapter<OrderCartViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderCartViewHolder {
        val itemView = inflater.inflate(R.layout.body_order_cart_item, parent, false)
        return OrderCartViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: OrderCartViewHolder, position: Int) {
        val model = items[position]
        holder.run {
            setImage(model.image, context)
            setTitle(model.title)
            setPerPrice(model.price, model.unit, model.discount)
            setQuantity(model.price, model.unit, model.quantity, model.discount)
            setBrand(model.brand)
            setDiscount(model.discount)
            setTotal(model.discount, model.price, model.quantity)
        }
    }

}