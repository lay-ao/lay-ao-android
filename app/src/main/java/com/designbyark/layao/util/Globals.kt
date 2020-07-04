package com.designbyark.layao.util

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.designbyark.layao.data.Products
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.iid.FirebaseInstanceId
import java.text.SimpleDateFormat
import java.util.*

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

// Location
const val REQUEST_CODE_LOCATION = 100

fun getToken() {
    FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.d(LOG_TAG, "getInstanceId failed", task.exception)
            return@addOnCompleteListener
        }

        // Get new Instance ID token
        val token = task.result?.token
        sendTokenToFirestore(token ?: return@addOnCompleteListener)
    }
}

fun sendTokenToFirestore(token: String) {

    val auth = FirebaseAuth.getInstance()

    val firestore = FirebaseFirestore.getInstance()
    val userCollection = firestore.collection("Users")
    userCollection.document(auth.currentUser?.uid!!).update("token", token)
        .addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(LOG_TAG, "update failed", task.exception)
                return@addOnCompleteListener
            }

            Log.d(LOG_TAG, "Token updated successfully")
        }
}

// RecyclerView Helper Methods
fun setHorizontalListLayout(recyclerView: RecyclerView, context: Context) {
    val layoutManager = LinearLayoutManager(
        context,
        LinearLayoutManager.HORIZONTAL, false
    )
    recyclerView.layoutManager = layoutManager
}

// Firebase Helper Methods
fun getProductOptions(query: Query): FirestoreRecyclerOptions<Products> {
    val options = FirestoreRecyclerOptions.Builder<Products>()
        .setQuery(query, Products::class.java)
        .build()
    return options
}

fun formatDate(timestamp: Date): String {
    val formatter = SimpleDateFormat("EEEE, dd MMMM, yyyy", Locale.getDefault())
    return formatter.format(timestamp)
}

fun formatTime(timestamp: Date): String {
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return formatter.format(timestamp)
}

fun setQuantityPrice(price: Double, quantity: Long, discount: Double, unit: String): String {
    if (discount > 0) {
        return String.format(
            Locale.getDefault(),
            "Rs. %.0f / %d %s",
            findDiscountPrice(price, discount) * quantity,
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

fun findDiscountPrice(price: Double, discount: Double): Double {
    val formula = discount / 100
    val salePrice = price * formula
    return price - salePrice
    // TODO: Change formula
    // (price - (price * (discount / 100.0))) * quantity
}

fun getSavingPrice(discountPrice: Double, originalPrice: Double): Double {
    return originalPrice - discountPrice
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

const val CHANNEL_ID = "order_channel"
fun displayNotification(context: Context, @DrawableRes icon: Int, title: String, content: String) {
    with(NotificationManagerCompat.from(context)) {
        notify(
            0, NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .setAutoCancel(true)
                .build()
        )
    }
}

fun displayNotification(
    context: Context,
    @DrawableRes icon: Int,
    title: String,
    content: String,
    pendingIntent: PendingIntent
) {
    with(NotificationManagerCompat.from(context)) {
        notify(
            0, NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()
        )
    }
}


fun getOrderStatus(status: Long): String {
    return when (status) {
        -1L -> "Scheduled"
        0L -> "Processing Order" // Orange
        1L -> "Order Active"     // Blue
        2L -> "Order on the way" // Green
        3L -> "Order Arrived"    // Purple
        4L -> "Order Delayed"    // Red
        5L -> "Order Received"   // Green
        6L -> "Order Cancelled"  // Red
        else -> return "Status Unknown"
    }
}

fun formatOrderId(orderId: String, phoneNumber: String): String {
    return orderId.slice(10..12).toUpperCase(Locale.getDefault()) +
            phoneNumber.slice(4..6) +
            orderId.takeLast(3)
}

fun formatGender(code: Int?): String {
    return when (code) {
        1 -> "Female"
        2 -> "Male"
        3 -> "Super Human"
        else -> "Not specified"
    }
}

fun circularProgressBar(context: Context): CircularProgressDrawable {
    val circularProgressBar = CircularProgressDrawable(context)
    circularProgressBar.strokeWidth = 2.5f
    circularProgressBar.centerRadius = 25.0f
    circularProgressBar.start()
    return circularProgressBar
}

@Suppress("DEPRECATION")
fun isConnectedToInternet(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    } else {
        val nwInfo = connectivityManager.activeNetworkInfo ?: return false
        return nwInfo.isConnected
    }
}

fun disableInteraction(activity: Activity, layout: View) {
    activity.window.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    )
    layout.visibility = View.VISIBLE
}

fun enableInteraction(activity: Activity, layout: View) {
    activity.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    layout.visibility = View.GONE
}

// region LOCATION HELPER FUNCTIONS

fun isLocationPermissionAvailable(activity: Activity): Boolean {
    if (initializePermission(activity)) {
        return true
    } else {
        requestPermissions(activity)
    }
    return false
}

private fun initializePermission(activity: Activity): Boolean {
    val fineLocationAvailable = ActivityCompat.checkSelfPermission(
        activity.applicationContext,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val coarseLocationAvailable = ActivityCompat.checkSelfPermission(
        activity.applicationContext,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return fineLocationAvailable && coarseLocationAvailable
}

private fun requestPermissions(activity: Activity) {
    val contextProvider =
        ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    if (contextProvider) {
        Toast.makeText(
            activity.applicationContext,
            "Permission is required to obtain location",
            Toast.LENGTH_SHORT
        ).show()
    }

    ActivityCompat.requestPermissions(
        activity,
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        REQUEST_CODE_LOCATION
    )
}

fun isGPSEnabled(context: Context): Boolean {
    val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

// endregion
