package com.designbyark.layao.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.designbyark.layao.data.Favorites
import com.designbyark.layao.db.LayAoRoomDatabase
import com.designbyark.layao.repos.FavoritesRepository
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FavoritesRepository

    val allFavorites: LiveData<List<Favorites>>

    init {
        val favDao = LayAoRoomDatabase.getDatabase(application).favoriteDao()
        repository = FavoritesRepository(favDao)
        allFavorites = repository.allFavorites
    }

    fun insert(favorites: Favorites) = viewModelScope.launch {
        repository.insert(favorites)
    }

    fun deleteAllFavorites() = viewModelScope.launch {
        repository.deleteAllFavorites()
    }

    fun deleteAFavorite(favorites: Favorites) = viewModelScope.launch {
        repository.deleteAFavorite(favorites)
    }

}