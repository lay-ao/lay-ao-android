package com.designbyark.layao.ui.home.discountItems

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.designbyark.layao.R
import com.designbyark.layao.data.Banner
import com.designbyark.layao.data.Product
import com.designbyark.layao.ui.home.ProductViewHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import java.util.*

class DiscountItemsAdapter internal constructor(
    options: FirestoreRecyclerOptions<Product>,
    var context: Context
) :
    FirestoreRecyclerAdapter<Product, ProductViewHolder>(options) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.body_discount_items, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: Product) {
        holder.run {
            setDiscount(model.discount)
            setImage(model.image, context)
        }
    }

}