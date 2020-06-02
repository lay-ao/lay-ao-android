package com.designbyark.layao.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.data.Products
import com.designbyark.layao.databinding.BodyDiscountItemsBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class DiscountItemsAdapter internal constructor(
    options: FirestoreRecyclerOptions<Products>,
    private val itemClickListener: DiscountItemClickListener
) :
    FirestoreRecyclerAdapter<Products, DiscountItemsAdapter.DiscountItemViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscountItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BodyDiscountItemsBinding.inflate(layoutInflater, parent, false)
        return DiscountItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiscountItemViewHolder, position: Int, model: Products) {
        holder.bind(model)
        model.productId = snapshots.getSnapshot(position).id
        holder.itemView.setOnClickListener {
            itemClickListener.onDiscountItemClickListener(model)
        }
    }

    class DiscountItemViewHolder(private val binding: BodyDiscountItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Products) {
            binding.product = product
            binding.executePendingBindings()
        }
    }

    interface DiscountItemClickListener {
        fun onDiscountItemClickListener(product: Products)
    }

}