package com.designbyark.layao.ui.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.designbyark.layao.data.cart.Cart
import com.designbyark.layao.data.cart.CartRepository
import com.designbyark.layao.helper.LayAoRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CartViewModel(application: Application): AndroidViewModel(application) {

    private val repository: CartRepository

    val allCartItems: LiveData<List<Cart>>
    val total: LiveData<Double>

    init {
        val cartDao = LayAoRoomDatabase.getDatabase(application).cartDao()
        repository = CartRepository(cartDao)
        allCartItems = repository.allCartItems
        total = repository.total
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

    fun itemCount(): Int = runBlocking {
        repository.itemCount()
    }


}