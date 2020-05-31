package com.designbyark.layao.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.data.Products
import com.designbyark.layao.databinding.BodyNewArrivalBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class NewArrivalAdapter internal constructor(
    options: FirestoreRecyclerOptions<Products>,
    private val itemClickListener: NewArrivalClickListener
) : FirestoreRecyclerAdapter<Products, NewArrivalAdapter.NewArrivalViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewArrivalViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BodyNewArrivalBinding.inflate(layoutInflater, parent, false)
        return NewArrivalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewArrivalViewHolder, position: Int, model: Products) {
        holder.bind(model)
        holder.itemView.setOnClickListener {
            itemClickListener.mNewArrivalClickListener(
                snapshots
                    .getSnapshot(holder.adapterPosition).id
            )
        }
    }

    inner class NewArrivalViewHolder internal constructor(private val binding: BodyNewArrivalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Products) {
            binding.product = product
            binding.executePendingBindings()
        }

    }

    interface NewArrivalClickListener {
        fun mNewArrivalClickListener(productId: String)
    }

}