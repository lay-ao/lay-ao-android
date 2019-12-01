package com.designbyark.layao.ui.favorites

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.data.favorite.Favorite

class FavoriteAdapter internal constructor(
    private val context: Context,
    private val favoriteViewModel: FavoriteViewModel
) : RecyclerView.Adapter<FavoriteViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var items = emptyList<Favorite>() // Cached copy of favorites

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val itemView = inflater.inflate(R.layout.body_favorite, parent, false)
        return FavoriteViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val model = items[position]
        holder.run {
            setImage(model.image, context)
            setTitle(model.title)
            favButton.setOnClickListener {
                favoriteViewModel.deleteFavorite(model)
                notifyDataSetChanged()
            }

        }
    }

    internal fun setItems(items: List<Favorite>) {
        this.items = items
        notifyDataSetChanged()
    }

}