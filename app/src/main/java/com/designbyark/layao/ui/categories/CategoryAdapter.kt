package com.designbyark.layao.ui.categories

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.designbyark.layao.R
import com.designbyark.layao.common.circularProgressBar
import com.designbyark.layao.data.Category
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class CategoryAdapter internal constructor(
    options: FirestoreRecyclerOptions<Category>,
    private val context: Context,
    private val itemClickListener: CategoryClickListener
) : FirestoreRecyclerAdapter<Category, CategoryAdapter.CategoryViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.body_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int, model: Category) {
        holder.run {
            setImage(context, model.image)
            setTitle(model.title)
            itemView.setOnClickListener {
                itemClickListener.mCategoryClickListener(snapshots
                    .getSnapshot(holder.adapterPosition).id)
            }
        }
    }

    inner class CategoryViewHolder internal constructor(private val view: View) :
        RecyclerView.ViewHolder(view) {


        internal fun setImage(context: Context, image: String) {
            val imageView: ImageView = view.findViewById(R.id.image)
            Glide.with(context).load(image).placeholder(circularProgressBar(context)).into(imageView)
        }

        internal fun setTitle(title: String) {
            val textView: TextView = view.findViewById(R.id.title)
            textView.text = title
        }

    }

    interface CategoryClickListener {
        fun mCategoryClickListener(categoryId: String)
    }

}