package com.designbyark.layao.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.data.Brand
import com.designbyark.layao.databinding.BodyBrandItemBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class BrandsListAdapter internal constructor(
    options: FirestoreRecyclerOptions<Brand>,
    private val itemClickListener: BrandsItemClickListener
) : FirestoreRecyclerAdapter<Brand, BrandsListAdapter.BrandsItemViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandsItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BodyBrandItemBinding.inflate(layoutInflater, parent, false)
        return BrandsItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BrandsItemViewHolder, position: Int, model: Brand) {
        holder.bind(model)
        holder.itemView.setOnClickListener {
            itemClickListener.mBrandsItemClickListener(
                snapshots
                    .getSnapshot(holder.adapterPosition).id
            )
        }
    }

    inner class BrandsItemViewHolder internal constructor(private val binding: BodyBrandItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(brand: Brand) {
            binding.brand = brand
            binding.executePendingBindings()
        }
    }

    interface BrandsItemClickListener {
        fun mBrandsItemClickListener(brandId: String)
    }

}