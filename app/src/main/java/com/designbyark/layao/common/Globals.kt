package com.designbyark.layao.common

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.data.Product
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

// Logging
const val LOG_TAG = "LOG TAG"

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
        return String.format(
            Locale.getDefault(),
            "Rs. %.0f / %d %s",
            price * quantity,
            quantity,
            unit
        )
    }
}

fun setDiscountPrice(price: Double, discount: Double): Double {
    val formula = discount / 100
    val salePrice = price * formula
    return price - salePrice
}

fun emptyValidation(
    value: String,
    inputLayout: TextInputLayout
): Boolean {
    return if (value.isEmpty() || value == "") {
        inputLayout.error = "Field cannot be empty"
        true
    } else {
        inputLayout.error = null
        false
    }
}

fun phoneValidation(
    value: String,
    inputLayout: TextInputLayout
): Boolean {

    return if (emptyValidation(value, inputLayout)) {
        true
    } else if (!value.matches("^0(3|42)\\d+".toRegex())) {
        inputLayout.error = "Invalid phone number"
        true
    } else {
        inputLayout.error = null
        false
    }
}