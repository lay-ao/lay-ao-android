package com.designbyark.layao.repos

import androidx.lifecycle.LiveData
import com.designbyark.layao.dao.FavoriteDao
import com.designbyark.layao.data.Favorites

class FavoritesRepository(private val favDao: FavoriteDao) {

    val allFavorites: LiveData<List<Favorites>> = favDao.getAllFavorites()

    suspend fun insert(favorite: Favorites) {
        favDao.insert(favorite)
    }

    suspend fun deleteAllFavorites() {
        favDao.deleteAllFavorites()
    }

    suspend fun deleteAFavorite(favorite: Favorites) {
        favDao.deleteAFavorite(favorite)
    }

}