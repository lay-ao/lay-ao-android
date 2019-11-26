package com.designbyark.layao.ui.cart

import android.content.Context
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.designbyark.layao.R
import java.util.*

class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val deleteItem: ImageButton = itemView.findViewById(R.id.delete_item)

    internal fun setImage(image: String, context: Context) {
        val imagePlaceHolder: ImageView = itemView.findViewById(R.id.image)
        Glide.with(context).load(image).into(imagePlaceHolder)
    }

    internal fun setTitle(title: String) {
        val textView: TextView = itemView.findViewById(R.id.title)
        textView.text = title
    }

    internal fun setUnitLabel(unit: String) {
        val perUnitLabel: TextView = itemView.findViewById(R.id.per_unit_label)
        perUnitLabel.text = String.format(Locale.getDefault(),
            "Per %s", unit)
    }

    internal fun setPerPrice(price: Long, unit: String) {
        val perPrice: TextView = itemView.findViewById(R.id.per_price)
        perPrice.text = String.format(Locale.getDefault(),
            "Rs. %d/%s", price, unit)

    }

    internal fun setQuantity(price: Long, unit: String, quantity: Long) {
        val quantityPlaceHolder: TextView = itemView.findViewById(R.id.quantity)
        quantityPlaceHolder.text = String.format(Locale.getDefault(),
            "Rs. %d/%d %s - %d", price, quantity, unit, quantity)
    }

    internal fun setBrand(brand: String) {
        val brandPlaceHolder: TextView = itemView.findViewById(R.id.brand)
        brandPlaceHolder.text = brand
    }

    internal fun setDiscount(discount: Long) {
        val discountPlaceHolder: TextView = itemView.findViewById(R.id.discount)
        if (discount > 0) {
            discountPlaceHolder.text = String.format(
                Locale.getDefault(),
                "%d%% applied"
            )
        } else {
            discountPlaceHolder.visibility = View.GONE
        }
    }

}