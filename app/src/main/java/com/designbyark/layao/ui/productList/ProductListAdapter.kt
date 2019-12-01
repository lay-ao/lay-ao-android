package com.designbyark.layao.ui.productList

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.designbyark.layao.R
import com.designbyark.layao.data.Product
import com.designbyark.layao.data.favorite.Favorite
import com.designbyark.layao.ui.favorites.FavoriteViewModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ProductListAdapter internal constructor(
    options: FirestoreRecyclerOptions<Product>,
    val context: Context,
    val itemClickListener: ProductListItemClickListener,
    val favoriteViewModel: FavoriteViewModel
) : FirestoreRecyclerAdapter<Product, ProductListViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.body_product_list, parent, false)
        return ProductListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int, model: Product) {
        var isClickedFavorite = false
        val id = snapshots.getSnapshot(holder.adapterPosition).id
        holder.run {
            setImage(model.image, context)
            setPrice(model.price, model.unit, model.discount)
            setTitle(model.title)
            setDiscount(model.discount)

            favButton.setOnClickListener {

                if (!isClickedFavorite) {
                    val favorite = Favorite()
                    favorite.title = model.title
                    favorite.dbId = id
                    favorite.image = model.image
                    favorite.favorite = 1

                    favoriteViewModel.insert(favorite)
                    favButton.setImageResource(R.drawable.ic_favorite_clicked_color_24dp)
                    isClickedFavorite = true
                    notifyDataSetChanged()
                    return@setOnClickListener
                }

                if (isClickedFavorite) {
                    favoriteViewModel.deleteFavorite(favoriteViewModel.findFavoriteById(id))
                    favButton.setImageResource(R.drawable.ic_favorite_color_24dp)
                    isClickedFavorite = false
                    notifyDataSetChanged()
                    return@setOnClickListener
                }

            }

            if (favoriteViewModel.isFavorite(id) > 0) {
                favButton.setImageResource(R.drawable.ic_favorite_clicked_color_24dp)
                isClickedFavorite = true
            }

            itemView.setOnClickListener {
                itemClickListener.mProductListItemClickListener(
                    snapshots
                        .getSnapshot(holder.adapterPosition).id
                )
            }
        }
    }

    interface ProductListItemClickListener {
        fun mProductListItemClickListener(productId: String)
    }

}