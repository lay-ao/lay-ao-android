package com.designbyark.layao.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.designbyark.layao.R
import com.designbyark.layao.common.LOG_TAG
import com.designbyark.layao.common.isConnectedToInternet
import com.designbyark.layao.ui.favorites.Favorites
import com.designbyark.layao.ui.favorites.FavoritesViewHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference

class FavoriteAdapter internal constructor(
    options: FirestoreRecyclerOptions<Favorites>,
    private val collection: CollectionReference
) : FirestoreRecyclerAdapter<Favorites, FavoritesViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.body_favorite, parent, false)
        return FavoritesViewHolder(view)
    }

    @ExperimentalStdlibApi
    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int, model: Favorites) {

        holder.run {
            setImage(model.image, holder.itemView.context)
            setTitle(model.title)
            setBrand(model.brand)
            setPrice(model.price, model.unit, model.discount)
            favButton.isSelected = true

            favButton.setOnClickListener {
                if (isConnectedToInternet(holder.itemView.context)) {
                    collection.document(snapshots.getSnapshot(position).id)
                        .delete().addOnCompleteListener { task ->
                            if (task.isSuccessful && task.isComplete) {
                                Toast.makeText(
                                    holder.itemView.context,
                                    "Item removed from Favorites!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e(LOG_TAG, exception.localizedMessage, exception)
                        }
                } else {
                    Toast.makeText(holder.itemView.context,
                        "No network found!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

}