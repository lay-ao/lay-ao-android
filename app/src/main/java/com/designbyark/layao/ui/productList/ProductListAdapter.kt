package com.designbyark.layao.ui.productList

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.designbyark.layao.R
import com.designbyark.layao.common.LOG_TAG
import com.designbyark.layao.common.USERS_COLLECTION
import com.designbyark.layao.data.Products
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ProductListAdapter internal constructor(
    options: FirestoreRecyclerOptions<Products>,
    @LayoutRes private val layoutId: Int,
    private val itemClickListener: ProductListItemClickListener
) : FirestoreRecyclerAdapter<Products, ProductListViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutId, parent, false)
        return ProductListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int, model: Products) {
        holder.run {
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