package com.designbyark.layao.ui.home.banner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.data.Banner
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class BannerAdapter internal constructor(options: FirestoreRecyclerOptions<Banner>) :
    FirestoreRecyclerAdapter<Banner, BannerAdapter.BannerViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.body_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int, model: Banner) {
        holder.setTitle(model.title)
    }

    inner class BannerViewHolder internal constructor(private val view: View) :
        RecyclerView.ViewHolder(view) {

        internal fun setTitle(title: String) {
            val textView: TextView = view.findViewById(R.id.title)
            textView.text = title
        }

    }

}