package com.designbyark.layao.ui.favorites

import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.databinding.BodyFavoriteBinding

class FavoritesViewHolder internal constructor(private var binding: BodyFavoriteBinding) :
    RecyclerView.ViewHolder(binding.root) {

    val favButton = binding.favoriteButton

    fun bind(favorites: Favorites) {
        binding.favorite = favorites
        binding.executePendingBindings()
    }

}