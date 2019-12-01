package com.designbyark.layao.data.favorite

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM favorite_table")
    fun getAllFavorites(): LiveData<List<Favorite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: Favorite)

    @Query("DELETE FROM favorite_table")
    suspend fun deleteAllFavorites()

    @Delete
    suspend fun deleteFavorite(favorite: Favorite)

    @Query("SELECT COUNT(*) FROM favorite_table")
    suspend fun itemCount(): Int

    @Query("SELECT COUNT(*) FROM favorite_table WHERE db_id = :productId")
    suspend fun isFavorite(productId: String): Int

    @Query("SELECT * FROM favorite_table WHERE db_id = :productId")
    suspend fun findFavoriteById(productId: String): Favorite

}