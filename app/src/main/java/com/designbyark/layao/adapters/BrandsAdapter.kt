package com.designbyark.layao.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.data.Brand
import com.designbyark.layao.databinding.BodyBrandsBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class BrandsAdapter internal constructor(
    options: FirestoreRecyclerOptions<Brand>,
    private val itemClickListener: BrandItemClickListener
) : FirestoreRecyclerAdapter<Brand, BrandsAdapter.BrandViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BodyBrandsBinding.inflate(layoutInflater, parent, false)
        return BrandViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int, model: Brand) {
        holder.bind(model)
        holder.itemView.setOnClickListener {
            itemClickListener.mBrandItemClickListener(
                snapshots.getSnapshot(holder.adapterPosition).id
            )
        }
    }

    class BrandViewHolder(private val binding: BodyBrandsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(brand: Brand) {
            binding.brand = brand
            binding.executePendingBindings()
        }
    }

    interface BrandItemClickListener {
        fun mBrandItemClickListener(brandId: String)
    }

}