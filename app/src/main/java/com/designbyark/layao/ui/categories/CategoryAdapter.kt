package com.designbyark.layao.ui.categories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.data.Category
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class CategoryAdapter internal constructor(
    options: FirestoreRecyclerOptions<Category>
) : FirestoreRecyclerAdapter<Category, CategoryAdapter.CategoryViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.body_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int, model: Category) {
        holder.setTitle(model.title)
    }

    inner class CategoryViewHolder internal constructor(private val view: View) :
        RecyclerView.ViewHolder(view) {

        internal fun setTitle(title: String) {
            val textView: TextView = view.findViewById(R.id.title)
            textView.text = title
        }

    }

}