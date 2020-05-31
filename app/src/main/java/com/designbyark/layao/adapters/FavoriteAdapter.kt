package com.designbyark.layao.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.common.LOG_TAG
import com.designbyark.layao.common.isConnectedToInternet
import com.designbyark.layao.data.Favorites
import com.designbyark.layao.databinding.BodyFavoriteBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.CollectionReference

class FavoriteAdapter internal constructor(
    options: FirestoreRecyclerOptions<Favorites>,
    private val collection: CollectionReference
) : FirestoreRecyclerAdapter<Favorites, FavoriteAdapter.FavoritesViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = BodyFavoriteBinding.inflate(layoutInflater, parent, false)
        return FavoritesViewHolder(binding)
    }

    @ExperimentalStdlibApi
    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int, model: Favorites) {

        holder.bind(model)

        holder.favButton.isSelected = true
        holder.favButton.setOnClickListener {
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
                Toast.makeText(
                    holder.itemView.context,
                    "No network found!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    inner class FavoritesViewHolder internal constructor(private var binding: BodyFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val favButton = binding.favoriteButton

        fun bind(favorites: Favorites) {
            binding.favorite = favorites
            binding.executePendingBindings()
        }

    }

}