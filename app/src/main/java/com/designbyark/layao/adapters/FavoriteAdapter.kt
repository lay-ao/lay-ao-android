package com.designbyark.layao.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.data.Favorites
import com.designbyark.layao.databinding.BodyFavoriteBinding
import com.designbyark.layao.viewmodels.FavoritesViewModel

class FavoriteAdapter internal constructor(
    private val favoritesViewModel: FavoritesViewModel
) : RecyclerView.Adapter<FavoriteAdapter.FavoritesViewHolder>() {

    private var items = emptyList<Favorites>() // Cached copy of items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BodyFavoriteBinding.inflate(layoutInflater, parent, false)
        return FavoritesViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.bind(items[position])
        holder.favoriteBtn.setOnClickListener {
            favoritesViewModel.deleteAFavorite(items[position])
            notifyItemRemoved(position)
        }
    }

    inner class FavoritesViewHolder internal constructor(private var binding: BodyFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val favoriteBtn = binding.favoriteButton

        fun bind(favorites: Favorites) {
            binding.favorite = favorites
            binding.executePendingBindings()
        }

    }

    internal fun setFavorites(favorites: List<Favorites>) {
        this.items = favorites
        notifyDataSetChanged()
    }

}