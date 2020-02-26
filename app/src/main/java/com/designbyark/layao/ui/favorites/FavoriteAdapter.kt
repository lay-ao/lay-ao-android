package com.designbyark.layao.ui.favorites

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.designbyark.layao.R
import com.designbyark.layao.common.LOG_TAG
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
                collection.document(snapshots.getSnapshot(position).id)
                    .delete().addOnCompleteListener { task ->
                        if (task.isSuccessful && task.isComplete) {
                            notifyItemRemoved(position)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e(LOG_TAG, exception.localizedMessage, exception)
                    }
            }
        }
    }

}