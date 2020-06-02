package com.designbyark.layao.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.data.Products
import com.designbyark.layao.databinding.BodySimilarProductListBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class SimilarProductListAdapter internal constructor(
    options: FirestoreRecyclerOptions<Products>,
    private val itemClickListener: ProductListItemClickListener,
    private val productId: String
) : FirestoreRecyclerAdapter<Products, SimilarProductListAdapter.SimilarProductListViewHolder>(
    options
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SimilarProductListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BodySimilarProductListBinding.inflate(layoutInflater, parent, false)
        return SimilarProductListViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SimilarProductListViewHolder,
        position: Int,
        model: Products
    ) {
        val id = snapshots.getSnapshot(holder.adapterPosition).id
        holder.bind(model)
        model.productId = id
        if (id == productId) {
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
            return
        }
        holder.itemView.setOnClickListener {
            itemClickListener.mProductListItemClickListener(model)
        }
    }

    inner class SimilarProductListViewHolder internal constructor(private val binding: BodySimilarProductListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Products) {
            binding.product = product
            binding.executePendingBindings()
        }

    }

    interface ProductListItemClickListener {
        fun mProductListItemClickListener(product: Products)
    }

}