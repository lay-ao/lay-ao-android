package com.designbyark.layao.ui.productList

import android.content.Context
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.designbyark.layao.R
import com.designbyark.layao.common.setDiscountPrice
import java.util.*

class ProductListViewHolder internal constructor(private val view: View) :
    RecyclerView.ViewHolder(view) {

    val favButton: ImageButton = view.findViewById(R.id.fav_button)

    internal fun setImage(image: String, context: Context) {
        val imageView: ImageView = view.findViewById(R.id.image)
        Glide.with(context).load(image).into(imageView)
    }

    internal fun setTitle(title: String) {
        val textView: TextView = view.findViewById(R.id.title)
        textView.text = title
    }

    internal fun setPrice(price: Long, unit: String, discount: Long) {
        val priceView: TextView = view.findViewById(R.id.price)
        if (discount > 0) {
            priceView.text = String.format(
                "Rs. %.0f/%s",
                setDiscountPrice(price.toDouble(), discount.toDouble()),
                unit
            )
        } else {
            priceView.text = String.format("Rs. %d/%s", price, unit)
        }
    }

    internal fun setDiscount(discount: Long) {
        val discountView: TextView = view.findViewById(R.id.discount)
        if (discount > 0) {
            discountView.text = String.format(Locale.getDefault(), "%d%% off", discount)
        } else {
            discountView.visibility = View.GONE
        }
    }

}