package com.designbyark.layao.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.data.Products
import com.designbyark.layao.databinding.BodyDiscountItemsBinding
import com.designbyark.layao.databinding.BodySeeMoreBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

private const val FOOTER = 0
private const val REGULAR = 1

class DiscountItemsAdapter internal constructor(
    options: FirestoreRecyclerOptions<Products>,
    private val itemClickListener: DiscountItemClickListener
) : FirestoreRecyclerAdapter<Products, DiscountItemsAdapter.MultipleViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultipleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding
        return when (viewType) {
            FOOTER -> {
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
            itemClickListener.onDiscountSeeMoreClickListener()
        }
    }

    inner class MultipleViewHolder : RecyclerView.ViewHolder {

        private var itemBinding: BodyDiscountItemsBinding? = null
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

    override fun getItemViewType(position: Int): Int {
        return if (position == (itemCount - 1)) {
            FOOTER
        } else {
            REGULAR
        }
    }

    interface DiscountItemClickListener {
        fun onDiscountItemClickListener(product: Products)
        fun onDiscountSeeMoreClickListener()
    }

}