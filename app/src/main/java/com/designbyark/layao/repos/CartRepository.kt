package com.designbyark.layao.repos

import androidx.lifecycle.LiveData
import com.designbyark.layao.data.Cart
import com.designbyark.layao.dao.CartDao

class CartRepository(private val cartDao: CartDao) {

    val allCartItems: LiveData<List<Cart>> = cartDao.getAllItemsFromCart()

    fun getGrandTotal(): LiveData<Double> {
        return cartDao.getGrandTotal()
    }

    suspend fun insert(cart: Cart) {
        cartDao.insert(cart)
    }

    suspend fun deleteCart() {
        cartDao.deleteAllItemsFromCart()
    }

    suspend fun deleteCartItem(cart: Cart) {
        cartDao.deleteItemFromCart(cart)
    }

    suspend fun updateCart(cart: Cart) {
        cartDao.updateCart(cart)
    }

    suspend fun itemCount(): Int {
        return cartDao.itemCount()
    }

}