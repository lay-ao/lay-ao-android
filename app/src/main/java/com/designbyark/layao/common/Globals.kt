package com.designbyark.layao.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.Patterns
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.designbyark.layao.R
import com.designbyark.layao.data.Product
import com.designbyark.layao.data.User
import com.designbyark.layao.data.favorite.Favorite
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashSet

// Logging
const val LOG_TAG = "LAYAO_LOG"

// Firebase collections
const val CATEGORIES_COLLECTION = "Categories"
const val BANNER_COLLECTION = "Banners"
const val PRODUCTS_COLLECTION = "Products"
const val BRANDS_COLLECTION = "Brands"
const val ORDERS_COLLECTION = "Orders"
const val USERS_COLLECTION = "Users"

// Firebase query fields
const val TITLE = "title"
const val ACTIVE = "active"
const val DISCOUNT = "discount"
const val NEW_ARRIVAL = "newArrival"

// Notification
const val CHANNEL_ID = "default"
const val CHANNEL_NAME = "Default"
const val CHANNEL_DESC = "Default channels is for testing"


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
    val formatter = SimpleDateFormat("EEEE, dd MMMM, yyyy", Locale.getDefault())
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

// region VALIDATIONS

fun emptyValidation(
    value: String,
    inputLayout: TextInputLayout
): Boolean {
    return when {
        value.isEmpty() || value == "" -> {
            inputLayout.error = "Field cannot be empty"
            true
        }
        else -> {
            inputLayout.error = null
            false
        }
    }
}

fun phoneValidation(
    value: String,
    inputLayout: TextInputLayout
): Boolean {

    return when {
        emptyValidation(value, inputLayout) -> {
            true
        }
        !value.matches("^0(3|42)\\d+".toRegex()) || value.length != 11 -> {
            inputLayout.error = "Invalid phone number"
            true
        }
        else -> {
            inputLayout.error = null
            false
        }
    }
}

fun emailValidation(
    value: String,
    inputLayout: TextInputLayout
): Boolean {

    return when {
        emptyValidation(value, inputLayout) -> {
            true
        }
        !Patterns.EMAIL_ADDRESS.matcher(value).matches() -> {
            inputLayout.error = "Invalid email address"
            true
        }
        else -> {
            inputLayout.error = null
            false
        }
    }
}

fun passwordValidation(
    value: String,
    inputLayout: TextInputLayout
): Boolean {

    return when {
        emptyValidation(value, inputLayout) -> {
            true
        }
        value.length <= 8 -> {
            inputLayout.error = "Password should exceed 8 characters"
            true
        }
        else -> {
            inputLayout.error = null
            false
        }
    }
}

fun confirmPasswordValidation(
    value: String,
    confirmValue: String,
    inputLayout: TextInputLayout
): Boolean {

    return when {
        passwordValidation(value, inputLayout) -> {
            true
        }
        value != confirmValue -> {
            inputLayout.error = "Passwords should match"
            true
        }
        else -> {
            inputLayout.error = null
            false
        }
    }
}

fun duplicationValue(
    value: String,
    confirmValue: String
): Boolean {
    return value != confirmValue
}

// endregion

private fun createNotification(
    context: Context,
    icon: Int,
    title: String,
    content: String
): NotificationCompat.Builder {
    return NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(icon)
        .setContentTitle(title)
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText(content)
        )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
}

fun displayNotification(context: Context, icon: Int, title: String, content: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = CHANNEL_NAME
        val descriptionText = CHANNEL_DESC
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(1, createNotification(context, icon, title, content).build())
    }
}

fun getOrderStatus(status: Int): String {
    return when (status) {
        0 -> "Processing Order"
        1 -> "Order Active"
        2 -> "Order on the way"
        3 -> "Order Arrived"
        4 -> "Order Delayed"
        5 -> "Order Received"
        6 -> "Order Cancelled"
        else -> return "Status Unknown"
    }
}

fun formatOrderId(orderId: String, phoneNumber: String): String {
    return orderId.slice(10..12).toUpperCase(Locale.getDefault()) +
            phoneNumber.slice(4..6) +
            orderId.takeLast(3)
}

fun formatGender(code: Int): String {
    return when (code) {
        1 -> "Female"
        2 -> "Male"
        3 -> "Super Human"
        else -> "Not specified"
    }
}

fun listOutput(favList: List<Favorite>): String {
    val stringBuilder = StringBuilder()
    for (favorite in favList) {
        stringBuilder.append(favorite.dbId)
        stringBuilder.append(", ")
        stringBuilder.append(favorite.favorite)
        stringBuilder.append(", ")
        stringBuilder.append(favorite.image)
        stringBuilder.append(", ")
        stringBuilder.append(favorite.title)
    }
    return stringBuilder.toString()
}
