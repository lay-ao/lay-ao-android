package com.designbyark.layao.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.data.Products
import com.designbyark.layao.databinding.BodyBrandsBinding
import com.designbyark.layao.databinding.BodyNewArrivalBinding
import com.designbyark.layao.databinding.BodySeeMoreBinding
import com.designbyark.layao.databinding.BodySmallSeeMoreBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

private const val FOOTER = 0
private const val REGULAR = 1

class NewArrivalAdapter internal constructor(
    options: FirestoreRecyclerOptions<Products>,
    private val itemClickListener: NewArrivalClickListener
) : FirestoreRecyclerAdapter<Products, NewArrivalAdapter.MultipleViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultipleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding
        return when (viewType) {
            FOOTER -> {
                binding = BodySeeMoreBinding.inflate(inflater, parent, false)
                MultipleViewHolder(binding)
            }
            else -> {
                binding = BodyNewArrivalBinding.inflate(inflater, parent, false)
                MultipleViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: MultipleViewHolder, position: Int, model: Products) {
        holder.bind(model)
        model.productId = snapshots.getSnapshot(position).id
        holder.itemView.setOnClickListener {
            itemClickListener.mNewArrivalClickListener(model)
        }

        holder.seeMoreBinding?.root?.setOnClickListener {
            itemClickListener.onNewArrivalSeeMoreClickListener()
        }
    }

    inner class MultipleViewHolder : RecyclerView.ViewHolder {

        private var itemBinding: BodyNewArrivalBinding? = null
        var seeMoreBinding: BodySeeMoreBinding? = null

        constructor(binding: BodyNewArrivalBinding) : super(binding.root) {
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

    interface NewArrivalClickListener {
        fun mNewArrivalClickListener(product: Products)
        fun onNewArrivalSeeMoreClickListener()
    }

}