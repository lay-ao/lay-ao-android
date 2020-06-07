package com.designbyark.layao.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.designbyark.layao.data.Cart

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_table")
    fun getAllItemsFromCart(): LiveData<List<Cart>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cart: Cart)

    @Query("DELETE FROM cart_table")
    suspend fun deleteAllItemsFromCart()

    @Delete
    suspend fun deleteItemFromCart(cart: Cart)

    @Update
    suspend fun updateCart(cart: Cart)

    @Query("SELECT * FROM cart_table WHERE productId = :productId LIMIT 1")
    suspend fun getItem(productId: String): Cart

    @Query("SELECT COUNT(*) FROM cart_table")
    suspend fun itemCount(): Int

    @Query("SELECT SUM(total) FROM cart_table")
    fun getGrandTotal(): LiveData<Double>

}