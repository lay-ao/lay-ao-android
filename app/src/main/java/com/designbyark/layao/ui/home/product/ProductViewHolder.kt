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

    internal fun setDiscount(discount: Double) {
        val textView: TextView = view.findViewById(R.id.discount)
        textView.text = String.format(Locale.getDefault(), "%.0f%% off", discount)
    }

    internal fun setImage(image: String, context: Context) {
        val imageView: ImageView = view.findViewById(R.id.image)
        Glide.with(context).load(image).placeholder(circularProgressBar(context)).into(imageView)
    }

    internal fun setTitle(value: String) {
        val textView: TextView = view.findViewById(R.id.title)
        textView.text = value
    }

    internal fun setSavingPrice(saving: Double) {
        val textView: TextView = view.findViewById(R.id.saving_price)
        textView.text = String.format(Locale.getDefault(), "Save Rs. %.0f", saving)
    }

    internal fun setPrice(price: Double, unit: String) {
        val textView: TextView = view.findViewById(R.id.price)
        textView.text = String.format(Locale.getDefault(), "Rs. %.0f/%s", price, unit)
    }
}