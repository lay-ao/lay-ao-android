package com.designbyark.layao.ui.user

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.data.favorite.Favorite
import com.designbyark.layao.ui.home.HomeFragment

class UserFavoriteAdapter internal constructor(
    private val context: Context,
    private val navController: NavController
) : RecyclerView.Adapter<FavoriteUserViewHolder>() {

    private var items = emptyList<Favorite>() // Cached copy of favorites

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteUserViewHolder {
        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.body_user_favorite, parent, false)
        return FavoriteUserViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: FavoriteUserViewHolder, position: Int) {
        val model = items[position]
        holder.run {
            setImage(model.image, context)
            setTitle(model.title)
            itemView.setOnClickListener {
                val args = Bundle()
                args.putString(HomeFragment.PRODUCT_ID, model.dbId)
                navController.navigate(
                    R.id.action_navigation_user_to_productDetailFragment,
                    args
                )
            }
        }
    }

    internal fun setItems(items: List<Favorite>) {
        this.items = items
        notifyDataSetChanged()
    }

}