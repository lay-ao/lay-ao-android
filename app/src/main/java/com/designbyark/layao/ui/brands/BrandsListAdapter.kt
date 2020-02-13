package com.designbyark.layao.ui.brands

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
import com.designbyark.layao.data.Brands
import com.designbyark.layao.data.Category
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class BrandsListAdapter internal constructor(
    options: FirestoreRecyclerOptions<Brands>,
    private val context: Context,
    private val itemClickListener: BrandsItemClickListener
) : FirestoreRecyclerAdapter<Brands, BrandsListAdapter.BrandsItemViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandsItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.body_brand_item, parent, false)
        return BrandsItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: BrandsItemViewHolder, position: Int, model: Brands) {
        holder.run {
            setImage(context, model.image)
            setTitle(model.title)
            itemView.setOnClickListener {
                itemClickListener.mBrandsItemClickListener(snapshots
                    .getSnapshot(holder.adapterPosition).id)
            }
        }
    }

    inner class BrandsItemViewHolder internal constructor(private val view: View) :
        RecyclerView.ViewHolder(view) {

        internal fun setTitle(title: String) {
            val textView: TextView = view.findViewById(R.id.title)
            textView.text = title
        }

        internal fun setImage(context: Context, image: String) {
            val imageView: ImageView = view.findViewById(R.id.image)
            Glide.with(context).load(image).placeholder(circularProgressBar(context)).into(imageView)
        }


    }

    interface BrandsItemClickListener {
        fun mBrandsItemClickListener(brandId: String)
    }

}