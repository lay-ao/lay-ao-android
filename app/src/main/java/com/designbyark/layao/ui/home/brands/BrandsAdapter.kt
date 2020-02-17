package com.designbyark.layao.ui.home.brands

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.designbyark.layao.R
import com.designbyark.layao.data.Category
import com.designbyark.layao.ui.home.product.ProductViewHolder
import com.designbyark.layao.ui.productList.ProductListAdapter
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class BrandsAdapter internal constructor(
    options: FirestoreRecyclerOptions<Category>,
    val context: Context,
    private val itemClickListener: BrandItemClickListener
) : FirestoreRecyclerAdapter<Category, ProductViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.body_brands, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: Category) {
        holder.setImage(model.image, context)
        holder.setTitle(model.title)
        holder.itemView.setOnClickListener {
            itemClickListener.mBrandItemClickListener(snapshots
                .getSnapshot(holder.adapterPosition).id)
        }
    }

    interface BrandItemClickListener {
        fun mBrandItemClickListener(brandId: String)
    }

}