package com.designbyark.layao.data.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

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

    @Query("SELECT COUNT(*) FROM cart_table")
    suspend fun itemCount(): Int

}