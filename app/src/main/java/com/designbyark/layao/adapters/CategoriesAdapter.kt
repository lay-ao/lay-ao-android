package com.designbyark.layao.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.data.Category
import com.designbyark.layao.databinding.BodyHomeCategoriesBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class CategoriesAdapter internal constructor(
    options: FirestoreRecyclerOptions<Category>,
    private val itemClickListener: CategoryItemClickListener
) : FirestoreRecyclerAdapter<Category, CategoriesAdapter.CategoriesViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BodyHomeCategoriesBinding.inflate(layoutInflater, parent, false)
        return CategoriesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int, model: Category) {
        holder.bind(model)
        holder.itemView.setOnClickListener {
            itemClickListener.onCategoryItemClickListener(snapshots.getSnapshot(holder.adapterPosition).id)
        }
    }

    inner class CategoriesViewHolder internal constructor(private val binding: BodyHomeCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.category = category
            binding.executePendingBindings()
        }

    }

    interface CategoryItemClickListener {
        fun onCategoryItemClickListener(categoryId: String)
    }

}