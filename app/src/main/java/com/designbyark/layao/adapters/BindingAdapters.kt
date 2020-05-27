package com.designbyark.layao.adapters

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.designbyark.layao.common.circularProgressBar
import com.designbyark.layao.common.getSavingPrice
import com.designbyark.layao.data.Products
import java.util.*

@BindingAdapter("app:setImage")
fun setImage(view: ImageView, source: String) {
    Glide.with(view.context)
        .load(source)
        .placeholder(circularProgressBar(view.context))
        .into(view)
}

@BindingAdapter("app:setTitle")
fun setTitle(view: TextView, title: String) {
    view.text = title
}

@BindingAdapter("app:setDiscount")
fun setDiscount(view: TextView, discount: Double) {
    view.text = String.format(Locale.getDefault(), "%.0f%% off", discount)
}

@BindingAdapter("app:setSavingPrice")
fun setSavingPrice(view: TextView, product: Products) {
    view.text = String.format("Save Rs. %.0f", getSavingPrice(product.price, product.discount))
}