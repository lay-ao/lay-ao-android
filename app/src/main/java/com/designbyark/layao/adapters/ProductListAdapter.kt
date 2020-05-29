package com.designbyark.layao.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.data.Products
import com.designbyark.layao.databinding.BodyProductListBinding
import com.designbyark.layao.ui.productList.ProductListFragment
import com.designbyark.layao.viewholders.ProductListViewHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ProductListAdapter internal constructor(
    options: FirestoreRecyclerOptions<Products>,
    private val itemClickListener: ProductListItemClickListener
) : FirestoreRecyclerAdapter<Products, ProductListAdapter.ProductListVH>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BodyProductListBinding.inflate(layoutInflater, parent, false)
        return ProductListVH(binding)
    }

    override fun onBindViewHolder(holder: ProductListVH, position: Int, model: Products) {
        holder.bind(model)
        holder.itemView.setOnClickListener {
            val data = mutableMapOf<String, String>()
            data["id"] = snapshots.getSnapshot(holder.adapterPosition).id
            data["tag"] = model.tag
            itemClickListener.mProductListItemClickListener(data)
        }
    }

    inner class ProductListVH internal constructor(private val binding: BodyProductListBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Products) {
            binding.product = product
            binding.executePendingBindings()
        }
    }

    interface ProductListItemClickListener {
        fun mProductListItemClickListener(productData: MutableMap<String, String>)
    }

}