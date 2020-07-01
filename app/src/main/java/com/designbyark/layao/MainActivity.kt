package com.designbyark.layao

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.designbyark.layao.databinding.ActivityMainBinding
import com.designbyark.layao.util.CHANNEL_ID
import com.designbyark.layao.util.LOG_TAG
import com.designbyark.layao.viewmodels.CartViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import org.joda.time.LocalDateTime


class MainActivity : AppCompatActivity() {

    private lateinit var cartViewModel: CartViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_cart,
                R.id.navigation_user, R.id.navigation_orders
            )
        )
        // Set up Action Bar View
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up Bottom Navigation View
        binding.bottomNavView.setupWithNavController(navController)

        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        val badgeCount = binding.bottomNavView.getOrCreateBadge(R.id.navigation_cart)

        cartViewModel.allCartItems.observe(this, Observer {
            if (it.isEmpty()) {
                badgeCount.isVisible = false
            } else {
                badgeCount.number = it.size
                badgeCount.isVisible = true
            }
        })

        createNotificationChannel()
        displayToken()
    }

    private fun displayToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d(LOG_TAG, "getInstanceId failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new Instance ID token
            val token = task.result?.token
            Log.d(LOG_TAG, token ?: "No token found!")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Orders", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Notifications based on the status of an Order"
                }
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(findNavController(R.id.nav_host_fragment))
                || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
    }
}
