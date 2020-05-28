package com.designbyark.layao.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.data.Category
import com.designbyark.layao.databinding.BodyCategoryBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class CategoryAdapter internal constructor(
    options: FirestoreRecyclerOptions<Category>,
    private val itemClickListener: CategoryClickListener
) : FirestoreRecyclerAdapter<Category, CategoryAdapter.CategoryViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BodyCategoryBinding.inflate(layoutInflater, parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int, model: Category) {
        holder.bind(model)
        holder.itemView.setOnClickListener {
            itemClickListener.mCategoryClickListener(
                snapshots
                    .getSnapshot(holder.adapterPosition).id
            )
        }
    }

    inner class CategoryViewHolder internal constructor(private val binding: BodyCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.category = category
            binding.executePendingBindings()
        }

    }

    interface CategoryClickListener {
        fun mCategoryClickListener(categoryId: String)
    }

}