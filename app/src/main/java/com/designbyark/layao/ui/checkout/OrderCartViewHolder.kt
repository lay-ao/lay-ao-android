package com.designbyark.layao.ui.checkout

import android.content.Context
import android.graphics.Paint
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.designbyark.layao.R
import com.designbyark.layao.common.circularProgressBar
import com.designbyark.layao.common.setDiscountPrice
import java.util.*

class OrderCartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val perPrice: TextView = itemView.findViewById(R.id.per_price)
    private val discountPrice: TextView = itemView.findViewById(R.id.discount_price)
    private val totalPlaceHolder: TextView = itemView.findViewById(R.id.total_label)
    private val discountPlaceHolder: TextView = itemView.findViewById(R.id.mDiscount)
    private val discountLabel: TextView = itemView.findViewById(R.id.discount_label)
    private val imagePlaceHolder: ImageView = itemView.findViewById(R.id.image)
    private val titleTextView: TextView = itemView.findViewById(R.id.title)
    private val quantityPlaceHolder: TextView = itemView.findViewById(R.id.quantity)
    private val brandPlaceHolder: TextView = itemView.findViewById(R.id.brand)


    internal fun setImage(image: String, context: Context) {
        Glide.with(context).load(image).placeholder(circularProgressBar(context))
            .into(imagePlaceHolder)
    }

    internal fun setTitle(title: String) {
        titleTextView.text = title
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
        if (discount > 0) {
            if (quantity > 1) {
                quantityPlaceHolder.text = String.format(
                    Locale.getDefault(),
                    "Rs. %.0f / %d %ss", (setDiscountPrice(price, discount) * quantity), quantity, unit
                )
            } else {
                quantityPlaceHolder.text = String.format(
                    Locale.getDefault(),
                    "Rs. %.0f / %s", (setDiscountPrice(price, discount) * quantity), unit
                )
            }

        } else {
            if (quantity > 1) {
                quantityPlaceHolder.text = String.format(
                    Locale.getDefault(),
                    "Rs. %.0f / %d %ss", (price * quantity), quantity, unit
                )
            } else {
                quantityPlaceHolder.text = String.format(
                    Locale.getDefault(),
                    "Rs. %.0f / %s", (price * quantity), unit
                )
            }
        }
    }

    internal fun setBrand(brand: String) {
        brandPlaceHolder.text = brand
    }

    internal fun setDiscount(discount: Double) {
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

    internal fun setTotal(discount: Double, price: Double, quantity: Long) {
        if (discount > 0) {
            totalPlaceHolder.text = String.format(
                Locale.getDefault(),
                "Rs. %.2f", setDiscountPrice(price, discount) * quantity
            )
        } else {
            totalPlaceHolder.text = String.format(
                Locale.getDefault(),
                "Rs. %.2f", price * quantity
            )
        }
    }
}