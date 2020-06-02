package com.designbyark.layao.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.data.Products
import com.designbyark.layao.databinding.BodyProductListBinding
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
        model.productId = snapshots.getSnapshot(position).id
        holder.itemView.setOnClickListener {
            itemClickListener.mProductListItemClickListener(model)
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
        fun mProductListItemClickListener(product: Products)
    }

}