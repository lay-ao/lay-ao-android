package com.designbyark.layao.common

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.data.Product
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query

// Sample Data
const val IMAGE = "https://firebasestorage.googleapis.com/v0/b/lay-ao.appspot.com/o/Fruits%2Fapple.jpg?alt=media&token=16e5b18f-0069-4c27-8387-caa1a233b43b"

// Firebase collections
const val CATEGORIES_COLLECTION = "Categories"
const val BANNER_COLLECTION = "Banners"
const val PRODUCTS_COLLECTION = "Products"
const val BRANDS_COLLECTION = "Brands"

// Firebase query fields
const val TITLE = "title"
const val ACTIVE = "active"
const val DISCOUNT = "discount"
const val NEW_ARRIVAL = "newArrival"

// RecyclerView Helper Methods
fun setListLayout(recyclerView: RecyclerView, context: Context) {
    val layoutManager = LinearLayoutManager(
        context,
        LinearLayoutManager.HORIZONTAL, false
    )
    recyclerView.layoutManager = layoutManager
}

// Firebase Helper Methods
fun getProductOptions(query: Query): FirestoreRecyclerOptions<Product> {
    val options = FirestoreRecyclerOptions.Builder<Product>()
        .setQuery(query, Product::class.java)
        .build()
    return options
}