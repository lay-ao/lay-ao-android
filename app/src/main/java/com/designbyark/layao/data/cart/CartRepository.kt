package com.designbyark.layao.data.cart

import androidx.lifecycle.LiveData

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