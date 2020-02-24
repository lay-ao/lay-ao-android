package com.designbyark.layao.ui.cart

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.data.cart.Cart

class CartAdapter internal constructor(
    private val context: Context,
    private val cartViewModel: CartViewModel
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
            setPerPrice(model.price, model.unit, model.discount)
            setQuantity(model.price, model.unit, model.quantity, model.discount)
            setChangingQuantity(model.unit, model.quantity)
            setBrand(model.brand)
            setDiscount(model.discount)
            setTotal(model.discount, model.price, model.quantity)

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