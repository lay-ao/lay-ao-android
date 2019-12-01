package com.designbyark.layao.data.favorite

import androidx.lifecycle.LiveData

class FavoriteRepository(private val favoriteDao: FavoriteDao) {

    val allFavorites: LiveData<List<Favorite>> = favoriteDao.getAllFavorites()

    suspend fun insert(favorite: Favorite) {
        favoriteDao.insert(favorite)
    }

    suspend fun deleteAllFavorite() {
        favoriteDao.deleteAllFavorites()
    }

    suspend fun deleteFavorite(favorite: Favorite) {
        favoriteDao.deleteFavorite(favorite)
    }

    suspend fun itemCount(): Int {
        return favoriteDao.itemCount()
    }

    suspend fun isFavorite(productId: String) : Int {
        return favoriteDao.isFavorite(productId)
    }

    suspend fun findFavoriteById(productId: String) : Favorite {
        return favoriteDao.findFavoriteById(productId)
    }

}