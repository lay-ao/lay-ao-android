package com.designbyark.layao.ui.productList

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.designbyark.layao.R
import com.designbyark.layao.common.circularProgressBar
import com.designbyark.layao.common.findDiscountPrice
import java.util.*

class ProductListViewHolder internal constructor(private val view: View) :
    RecyclerView.ViewHolder(view) {

    internal fun setImage(image: String, context: Context) {
        val imageView: ImageView = view.findViewById(R.id.image)
        Glide.with(context).load(image).placeholder(circularProgressBar(context)).into(imageView)
    }

    internal fun setTitle(title: String) {
        val textView: TextView = view.findViewById(R.id.title)
        textView.text = title
    }

    internal fun setPrice(price: Double, unit: String, discount: Double) {
        val priceView: TextView = view.findViewById(R.id.price)
        if (discount > 0) {
            priceView.text = String.format(
                "Rs. %.0f/%s",
                findDiscountPrice(price, discount),
                unit
            )
        } else {
            priceView.text = String.format("Rs. %.0f/%s", price, unit)
        }
    }

    internal fun setDiscount(discount: Double) {
        val discountView: TextView = view.findViewById(R.id.mDiscount)
        if (discount > 0) {
            discountView.text = String.format(Locale.getDefault(), "%.0f%% off ", discount)
        } else {
            discountView.visibility = View.GONE
        }
    }

}