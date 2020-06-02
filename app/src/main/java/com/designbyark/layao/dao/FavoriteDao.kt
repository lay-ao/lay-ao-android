package com.designbyark.layao.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.designbyark.layao.data.Favorites

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorites_table WHERE isFavorite = 1")
    fun getAllFavorites(): LiveData<List<Favorites>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: Favorites)

    @Query("DELETE FROM favorites_table")
    suspend fun deleteAllFavorites()

    @Delete
    suspend fun deleteAFavorite(favorite: Favorites)

    @Query("SELECT isFavorite FROM favorites_table WHERE productId = :productId LIMIT 1")
    fun getFavorite(productId: String): LiveData<Int>

}