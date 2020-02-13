package com.designbyark.layao.ui.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.designbyark.layao.data.favorite.Favorite
import com.designbyark.layao.data.favorite.FavoriteDao
import com.designbyark.layao.data.favorite.FavoriteRepository
import com.designbyark.layao.db.LayAoRoomDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FavoriteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FavoriteRepository

    val allFavorites: LiveData<List<Favorite>>

    init {
        val favoriteDao: FavoriteDao = LayAoRoomDatabase.getDatabase(application).favoriteDao()
        repository = FavoriteRepository(favoriteDao)
        allFavorites = repository.allFavorites
    }

    fun insert(favorite: Favorite) = viewModelScope.launch {
        repository.insert(favorite)
    }

    fun deleteAllFavorite() = viewModelScope.launch {
        repository.deleteAllFavorite()
    }

    fun deleteFavorite(favorite: Favorite) = viewModelScope.launch {
        repository.deleteFavorite(favorite)
    }

    fun itemCount(): Int = runBlocking {
        repository.itemCount()
    }

    fun isFavorite(productId: String) : Int = runBlocking{
        repository.isFavorite(productId)
    }

    fun findFavoriteById(productId: String) : Favorite = runBlocking {
        repository.findFavoriteById(productId)
    }


}