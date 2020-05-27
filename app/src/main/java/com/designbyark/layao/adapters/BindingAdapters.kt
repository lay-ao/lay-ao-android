package com.designbyark.layao.adapters

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.designbyark.layao.common.circularProgressBar

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