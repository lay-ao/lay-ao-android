package com.designbyark.layao.ui.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.designbyark.layao.data.cart.Cart
import com.designbyark.layao.data.cart.CartRepository
import com.designbyark.layao.db.LayAoRoomDatabase
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CartRepository

    val allCartItems: LiveData<List<Cart>>

    init {
        val cartDao = LayAoRoomDatabase.getDatabase(application).cartDao()
        repository = CartRepository(cartDao)
        allCartItems = repository.allCartItems
    }

    fun getGrandTotal(): LiveData<Double> = runBlocking {
        repository.getGrandTotal()
    }

    fun insert(cart: Cart) = viewModelScope.launch {
        repository.insert(cart)
    }

    fun deleteCart() = viewModelScope.launch {
        repository.deleteCart()
    }

    fun deleteCartItem(cart: Cart) = viewModelScope.launch {
        repository.deleteCartItem(cart)
    }

    fun updateCart(cart: Cart) = viewModelScope.launch {
        repository.updateCart(cart)
    }

    fun itemCount(): Int = runBlocking {
        repository.itemCount()
    }

}