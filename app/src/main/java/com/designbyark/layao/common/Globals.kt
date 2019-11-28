package com.designbyark.layao.common

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.data.Product
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

// Sample Data
const val IMAGE =
    "https://firebasestorage.googleapis.com/v0/b/lay-ao.appspot.com/o/Fruits%2Fapple.jpg?alt=media&token=16e5b18f-0069-4c27-8387-caa1a233b43b"

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


fun formatTimeDate(timestamp: Date): String {
    val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
    return formatter.format(timestamp)
}

fun setQuantityPrice(price: Double, quantity: Long, discount: Double, unit: String): String {
    if (discount > 0) {
        return String.format(
            Locale.getDefault(),
            "Rs. %.0f / %d %s",
            setDiscountPrice(price, discount) * quantity,
            quantity,
            unit
        )
    } else {
        return String.format(Locale.getDefault(), "Rs. %.0f / %d %s", price * quantity, quantity, unit)
    }
}

fun setDiscountPrice(price: Double, discount: Double): Double {
    val formula = discount / 100
    val salePrice = price * formula
    return price - salePrice
}