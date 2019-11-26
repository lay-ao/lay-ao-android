package com.designbyark.layao.ui.home.newArrival

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.designbyark.layao.R
import com.designbyark.layao.data.Product
import com.designbyark.layao.ui.home.ProductViewHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class NewArrivalAdapter internal constructor(
    options: FirestoreRecyclerOptions<Product>,
    var context: Context
) : FirestoreRecyclerAdapter<Product, ProductViewHolder>(options) {

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
        model: Product
    ) {
        holder.setImage(model.image, context)
    }

}