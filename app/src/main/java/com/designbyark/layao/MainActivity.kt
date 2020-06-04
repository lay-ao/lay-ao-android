package com.designbyark.layao

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
import com.designbyark.layao.util.LOG_TAG
import com.designbyark.layao.viewmodels.CartViewModel
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat


class MainActivity : AppCompatActivity() {

    private lateinit var cartViewModel: CartViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

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

//        val now = LocalTime.now()
//        val opening = LocalTime.parse("09:00 AM", DateTimeFormat.forPattern("hh:mm a"))
//        val closing = LocalTime.parse("07:00 PM", DateTimeFormat.forPattern("hh:mm a"))
//        when {
//            now > closing -> {
//                Log.d(LOG_TAG, "Schedule order for tomorrow")
//            }
//            now > opening -> {
//                Log.d(LOG_TAG, "Orders are being delivered")
//            }
//            else -> {
//                Log.d(LOG_TAG, "Unknown time")
//            }
//        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(findNavController(R.id.nav_host_fragment))
                || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
    }
}
