package com.designbyark.layao.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.data.Brand
import com.designbyark.layao.databinding.BodyBrandsBinding
import com.designbyark.layao.databinding.BodySmallSeeMoreBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

private const val FOOTER = 0
private const val REGULAR = 1

class BrandsAdapter internal constructor(
    options: FirestoreRecyclerOptions<Brand>,
    private val itemClickListener: BrandItemClickListener
) : FirestoreRecyclerAdapter<Brand, BrandsAdapter.MultipleViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultipleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding
        return when (viewType) {
            FOOTER -> {
                binding = BodySmallSeeMoreBinding.inflate(inflater, parent, false)
                MultipleViewHolder(binding)
            }
            else -> {
                binding = BodyBrandsBinding.inflate(inflater, parent, false)
                MultipleViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: MultipleViewHolder, position: Int, model: Brand) {
        holder.bind(model)
        holder.itemView.setOnClickListener {
            itemClickListener.mBrandItemClickListener(
                snapshots.getSnapshot(holder.adapterPosition).id
            )
        }

        holder.seeMoreBinding?.root?.setOnClickListener {
            itemClickListener.onBrandSeeMoreClickListener()
        }
    }

    inner class MultipleViewHolder : RecyclerView.ViewHolder {

        private var itemBinding: BodyBrandsBinding? = null
        var seeMoreBinding: BodySmallSeeMoreBinding? = null

        constructor(binding: BodyBrandsBinding) : super(binding.root) {
            itemBinding = binding
        }

        constructor(binding: BodySmallSeeMoreBinding) : super(binding.root) {
            seeMoreBinding = binding
        }

        fun bind(brand: Brand) {
            itemBinding?.brand = brand
            itemBinding?.executePendingBindings()
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (position == (itemCount - 1)) {
            FOOTER
        } else {
            REGULAR
        }
    }

    interface BrandItemClickListener {
        fun mBrandItemClickListener(brandId: String)
        fun onBrandSeeMoreClickListener()
    }

}