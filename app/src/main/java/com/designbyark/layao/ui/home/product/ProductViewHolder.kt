package com.designbyark.layao.ui.home.product

import android.content.Context
import android.graphics.Paint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.designbyark.layao.R
import com.designbyark.layao.common.circularProgressBar
import java.util.*

class ProductViewHolder internal constructor(private val view: View) :
    RecyclerView.ViewHolder(view) {

    internal fun setDiscount(discount: Long) {
        val textView: TextView = view.findViewById(R.id.discount)
        textView.text = String.format(Locale.getDefault(), "%d%%", discount)
    }

    internal fun setImage(image: String, context: Context) {
        val imageView: ImageView = view.findViewById(R.id.image)
        Glide.with(context).load(image).placeholder(circularProgressBar(context)).into(imageView)
    }

    internal fun setTitle(value: String) {
        val textView: TextView = view.findViewById(R.id.title)
        textView.text = value
    }

    internal fun setBeforePrice(beforePrice: Double) {
        val textView: TextView = view.findViewById(R.id.before_price)
        textView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        textView.text = String.format("Rs. %.0f", beforePrice)
    }

    internal fun setLatestDiscountPrice(price: Double) {
        val textView: TextView = view.findViewById(R.id.discount_price)
        textView.text = String.format("Rs. %.0f", price)
    }

}