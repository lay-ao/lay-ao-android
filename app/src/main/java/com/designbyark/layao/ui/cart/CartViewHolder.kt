package com.designbyark.layao.ui.cart

import android.content.Context
import android.graphics.Paint
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.designbyark.layao.R
import com.designbyark.layao.common.setDiscountPrice
import java.util.*

class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val deleteItem: ImageButton = itemView.findViewById(R.id.delete_item)
    val perPrice: TextView = itemView.findViewById(R.id.per_price)
    val discountPrice: TextView = itemView.findViewById(R.id.discount_price)


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
        perUnitLabel.text = String.format(
            Locale.getDefault(),
            "Per %s", unit
        )
    }

    internal fun setPerPrice(price: Double, unit: String, discount: Double) {
        if (discount > 0) {
            perPrice.text = String.format(
                Locale.getDefault(),
                "Rs. %.0f/%s", price, unit
            )
            perPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            discountPrice.text = String.format(
                Locale.getDefault(),
                "Rs. %.0f/%s", setDiscountPrice(price, discount), unit
            )
        } else {
            perPrice.text = String.format(
                Locale.getDefault(),
                "Rs. %.0f/%s", price, unit
            )
            discountPrice.visibility = View.INVISIBLE
        }

    }

    internal fun setQuantity(price: Double, unit: String, quantity: Long, discount: Double) {
        val quantityPlaceHolder: TextView = itemView.findViewById(R.id.quantity)
        if (discount > 0) {
            quantityPlaceHolder.text = String.format(
                Locale.getDefault(),
                "Rs. %.0f per %d %s", (setDiscountPrice(price, discount) * quantity), quantity, unit
            )
        } else {
            quantityPlaceHolder.text = String.format(
                Locale.getDefault(),
                "Rs. %.0f per %d %s", (price * quantity), quantity, unit
            )
        }
    }

    internal fun setBrand(brand: String) {
        val brandPlaceHolder: TextView = itemView.findViewById(R.id.brand)
        brandPlaceHolder.text = brand
    }

    internal fun setDiscount(discount: Double) {
        val discountPlaceHolder: TextView = itemView.findViewById(R.id.discount)
        val discountLabel: TextView = itemView.findViewById(R.id.discount_label)
        if (discount > 0) {
            discountPlaceHolder.text = String.format(
                Locale.getDefault(),
                "%.0f%% applied", discount
            )
        } else {
            discountPlaceHolder.visibility = View.INVISIBLE
            discountLabel.visibility = View.INVISIBLE
        }
    }

    internal fun setTotal(total: Double) {
        val totalPlaceHolder: TextView = itemView.findViewById(R.id.total)
        totalPlaceHolder.text = String.format(Locale.getDefault(),
            "Rs. %.0f", total)
    }
}