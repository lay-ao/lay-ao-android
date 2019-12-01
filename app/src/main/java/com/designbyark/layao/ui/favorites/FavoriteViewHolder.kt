package com.designbyark.layao.ui.favorites

import android.content.Context
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.designbyark.layao.R

class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val favButton: ImageButton = itemView.findViewById(R.id.fav_button)

    internal fun setTitle(title: String) {
        val textView: TextView = itemView.findViewById(R.id.title)
        textView.text = title
    }

    internal fun setImage(image: String, context: Context) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
        Glide.with(context).load(image).into(imageView)
    }

}