package com.designbyark.layao.ui.productList

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.designbyark.layao.R
import com.designbyark.layao.data.Product
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ProductListAdapter internal constructor(
    options: FirestoreRecyclerOptions<Product>,
    val context: Context,
    val itemClickListener: ProductListItemClickListener
) : FirestoreRecyclerAdapter<Product, ProductListViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.body_product_list, parent, false)
        return ProductListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int, model: Product) {
        holder.run {
            setImage(model.image, context)
            setPrice(model.price, model.unit, model.discount)
            setTitle(model.title)
            setDiscount(model.discount)

            favButton.setOnClickListener {
                favButton.setImageResource(R.drawable.ic_favorite_clicked_color_24dp)
            }

            itemView.setOnClickListener {
                itemClickListener.mProductListItemClickListener(snapshots
                    .getSnapshot(holder.adapterPosition).id)
            }
        }
    }

    interface ProductListItemClickListener {
        fun mProductListItemClickListener(productId: String)
    }

}