package com.designbyark.layao.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.data.Products
import com.designbyark.layao.databinding.BodyDiscountItemsBinding
import com.designbyark.layao.databinding.BodySeeMoreBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class DiscountItemsAdapter internal constructor(
    options: FirestoreRecyclerOptions<Products>,
    private val itemClickListener: DiscountItemClickListener
) : FirestoreRecyclerAdapter<Products, DiscountItemsAdapter.MultipleViewHolder>(options) {

    private val CELL_TYPE_HEADER = 0
    private val CELL_TYPE_REGULAR_ITEM = 1


//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscountItemViewHolder {
//        val layoutInflater = LayoutInflater.from(parent.context)
//        val binding = BodyDiscountItemsBinding.inflate(layoutInflater, parent, false)
//        return DiscountItemViewHolder(binding)
//    }

//    override fun onBindViewHolder(holder: DiscountItemViewHolder, position: Int, model: Products) {
//
//        holder.bind(model)
//        model.productId = snapshots.getSnapshot(position).id
//        holder.itemView.setOnClickListener {
//            itemClickListener.onDiscountItemClickListener(model)
//        }
//    }

//    inner class DiscountItemViewHolder(private val binding: BodyDiscountItemsBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        fun bind(product: Products) {
//            binding.product = product
//            binding.executePendingBindings()
//        }
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultipleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding
        return when (viewType) {
            CELL_TYPE_HEADER -> {
                binding = BodySeeMoreBinding.inflate(inflater, parent, false)
                MultipleViewHolder(binding)
            }
            else -> {
                binding = BodyDiscountItemsBinding.inflate(inflater, parent, false)
                MultipleViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: MultipleViewHolder, position: Int, model: Products) {
        holder.bind(model)
        model.productId = snapshots.getSnapshot(position).id
        holder.itemView.setOnClickListener {
            itemClickListener.onDiscountItemClickListener(model)
        }

        holder.seeMoreBinding?.root?.setOnClickListener {
            itemClickListener.onSeeMoreClickListener()
        }
    }

    inner class MultipleViewHolder : RecyclerView.ViewHolder {

        var itemBinding: BodyDiscountItemsBinding? = null
        var seeMoreBinding: BodySeeMoreBinding? = null

        constructor(binding: BodyDiscountItemsBinding) : super(binding.root) {
            itemBinding = binding
        }

        constructor(binding: BodySeeMoreBinding) : super(binding.root) {
            seeMoreBinding = binding
        }

        fun bind(product: Products) {
            itemBinding?.product = product
            itemBinding?.executePendingBindings()
        }

    }

//    internal class MyViewHolder : RecyclerView.ViewHolder {
//        private var headerBinding: HeaderBinding? = null
//        private var regularItemBinding: RegularItemBinding? = null
//
//        constructor(binding: HeaderBinding) : super(binding.getRoot()) {
//            headerBinding = binding
//        }
//
//        constructor(binding: RegularItemBinding) : super(binding.getRoot()) {
//            regularItemBinding = binding
//        }
//    }

    override fun getItemViewType(position: Int): Int {
        return if (position == (itemCount - 1)) {
            CELL_TYPE_HEADER
        } else {
            CELL_TYPE_REGULAR_ITEM
        }
    }

    interface DiscountItemClickListener {
        fun onDiscountItemClickListener(product: Products)
        fun onSeeMoreClickListener()
    }

}