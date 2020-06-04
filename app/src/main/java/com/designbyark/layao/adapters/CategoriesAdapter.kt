package com.designbyark.layao.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.data.Category
import com.designbyark.layao.databinding.BodyHomeCategoriesBinding
import com.designbyark.layao.databinding.BodySmallSeeMoreBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

private const val FOOTER = 0
private const val REGULAR = 1

class CategoriesAdapter internal constructor(
    options: FirestoreRecyclerOptions<Category>,
    private val itemClickListener: CategoryItemClickListener
) : FirestoreRecyclerAdapter<Category, CategoriesAdapter.MultipleViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultipleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding
        return when (viewType) {
            FOOTER -> {
                binding = BodySmallSeeMoreBinding.inflate(inflater, parent, false)
                MultipleViewHolder(binding)
            }
            else -> {
                binding = BodyHomeCategoriesBinding.inflate(inflater, parent, false)
                MultipleViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: MultipleViewHolder, position: Int, model: Category) {
        holder.bind(model)
        holder.itemView.setOnClickListener {
            itemClickListener.onCategoryItemClickListener(snapshots.getSnapshot(holder.adapterPosition).id)
        }

        holder.seeMoreBinding?.root?.setOnClickListener {
            itemClickListener.onCategorySeeMoreClickListener()
        }
    }

    inner class MultipleViewHolder : RecyclerView.ViewHolder {

        private var itemBinding: BodyHomeCategoriesBinding? = null
        var seeMoreBinding: BodySmallSeeMoreBinding? = null

        constructor(binding: BodyHomeCategoriesBinding) : super(binding.root) {
            itemBinding = binding
        }

        constructor(binding: BodySmallSeeMoreBinding) : super(binding.root) {
            seeMoreBinding = binding
        }

        fun bind(category: Category) {
            itemBinding?.category = category
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

    interface CategoryItemClickListener {
        fun onCategoryItemClickListener(categoryId: String)
        fun onCategorySeeMoreClickListener()
    }

}