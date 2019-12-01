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
import com.designbyark.layao.data.Banner
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class BannerAdapter internal constructor(
    options: FirestoreRecyclerOptions<Banner>,
    private val itemClickListener: BannerItemClickListener,
    private val context: Context
) : FirestoreRecyclerAdapter<Banner, BannerAdapter.BannerViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.body_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int, model: Banner) {
        holder.run {
            setImage(model.image, context)
            itemView.setOnClickListener {
                itemClickListener.onBannerItemClickListener(snapshots.getSnapshot(holder.adapterPosition).id)
            }
        }
    }

    inner class BannerViewHolder internal constructor(private val view: View) :
        RecyclerView.ViewHolder(view) {

//        internal fun setTitle(title: String) {
//            val textView: TextView = view.findViewById(R.id.title)
//            textView.text = title
//        }

        internal fun setImage(image: String, context: Context) {
            val imageView: ImageView = view.findViewById(R.id.image)
            Glide.with(context).load(image).into(imageView)
        }
    }

    interface BannerItemClickListener {
        fun onBannerItemClickListener(bannerId: String)
    }

}