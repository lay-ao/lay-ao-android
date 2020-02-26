package com.designbyark.layao.ui.favorites

import android.content.Context
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

class FavoritesViewHolder internal constructor(private val view: View) :
    RecyclerView.ViewHolder(view) {

    val favButton: ImageButton = view.findViewById(R.id.fav_button)

    internal fun setImage(image: String, context: Context) {
        val imageView: ImageView = view.findViewById(R.id.image)
        Glide.with(context).load(image).placeholder(circularProgressBar(context)).into(imageView)
    }

    internal fun setTitle(value: String) {
        val textView: TextView = view.findViewById(R.id.title)
        textView.text = value
    }

    @ExperimentalStdlibApi
    internal fun setBrand(brand: String) {
        val textView: TextView = view.findViewById(R.id.brand)
        textView.text = brand.capitalize(Locale.getDefault())
    }

    internal fun setPrice(price: Double, unit: String, discount: Double) {
        val textView: TextView = view.findViewById(R.id.price)
        if (discount > 0) {
            textView.text = String.format(
                Locale.getDefault(),
                "Rs. %.2f/%s",
                setDiscountPrice(price, discount),
                unit
            )
        } else {
            textView.text = String.format(Locale.getDefault(), "Rs. %.2f/%s", price, unit)
        }
    }

}