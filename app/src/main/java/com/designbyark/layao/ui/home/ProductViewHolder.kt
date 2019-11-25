package com.designbyark.layao.ui.home

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.designbyark.layao.R
import java.util.*

class ProductViewHolder internal constructor(private val view: View) :
    RecyclerView.ViewHolder(view) {

    internal fun setDiscount(discount: Long) {
        val textView: TextView = view.findViewById(R.id.discount)
        textView.text = String.format(Locale.getDefault(), "%d%%", discount)
    }

    internal fun setImage(image: String, context: Context) {
        val imageView: ImageView = view.findViewById(R.id.image)
        Glide.with(context).load(image).into(imageView)
    }

}