package com.designbyark.layao.ui.productList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.data.Products
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class SimilarProductListAdapter internal constructor(
    options: FirestoreRecyclerOptions<Products>,
    private val itemClickListener: ProductListItemClickListener,
    private val productId: String
) : FirestoreRecyclerAdapter<Products, ProductListViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.body_similar_product_list, parent, false)
        return ProductListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int, model: Products) {
        holder.run {
            if (snapshots.getSnapshot(holder.adapterPosition).id == productId) {
                itemView.visibility = View.GONE
                itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
                return@run
            }
            setImage(model.image, holder.itemView.context)
            setPrice(model.price, model.unit, model.discount)
            setTitle(model.title)
            setDiscount(model.discount)

            itemView.setOnClickListener {
                val data = mutableMapOf<String, String>()
                data["id"] = snapshots.getSnapshot(holder.adapterPosition).id
                data["tag"] = model.tag
                itemClickListener.mProductListItemClickListener(data)
            }
        }
    }

    interface ProductListItemClickListener {
        fun mProductListItemClickListener(productData: MutableMap<String, String>)
    }

}