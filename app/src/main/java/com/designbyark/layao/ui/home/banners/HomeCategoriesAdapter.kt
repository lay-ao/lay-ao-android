package com.designbyark.layao.ui.home.banners

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
import com.designbyark.layao.data.Banner
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class HomeCategoriesAdapter internal constructor(
    options: FirestoreRecyclerOptions<Banner>,
    private val itemClickListener: HomeCategoryItemClickListener,
    private val context: Context
) : FirestoreRecyclerAdapter<Banner, HomeCategoriesAdapter.HomeCategoriesViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCategoriesViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.body_home_categories, parent, false)
        return HomeCategoriesViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeCategoriesViewHolder, position: Int, model: Banner) {
        holder.run {
            setImage(model.image, context)
            setText(model.title)
            itemView.setOnClickListener {
                itemClickListener.onHomeCategoryItemClickListener(snapshots.getSnapshot(holder.adapterPosition).id)
            }
        }
    }

    inner class HomeCategoriesViewHolder internal constructor(private val view: View) :
        RecyclerView.ViewHolder(view) {

        internal fun setImage(image: String, context: Context) {
            val imageView: ImageView = view.findViewById(R.id.image)
            Glide.with(context)
                .load(image)
                .placeholder(circularProgressBar(context))
                .into(imageView)
        }

        internal fun setText(value: String) {
            val textView: TextView = view.findViewById(R.id.title)
            textView.text = value
        }
    }

    interface HomeCategoryItemClickListener {
        fun onHomeCategoryItemClickListener(categoryId: String)
    }

}