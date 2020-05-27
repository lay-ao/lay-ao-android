package com.designbyark.layao.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.designbyark.layao.R
import com.designbyark.layao.data.Products
import com.designbyark.layao.ui.home.product.ProductViewHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class NewArrivalAdapter internal constructor(
    options: FirestoreRecyclerOptions<Products>,
    var context: Context,
    val itemClickListener: NewArrivalClickListener
) : FirestoreRecyclerAdapter<Products, ProductViewHolder>(options) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.body_new_arrival, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int,
        model: Products
    ) {
        holder.setImage(model.image, context)
        holder.setTitle(model.title)
        holder.setPrice(model.price, model.unit)
        holder.itemView.setOnClickListener {
            itemClickListener.mNewArrivalClickListener(
                snapshots
                    .getSnapshot(holder.adapterPosition).id
            )
        }
    }

    interface NewArrivalClickListener {
        fun mNewArrivalClickListener(productId: String)
    }

}