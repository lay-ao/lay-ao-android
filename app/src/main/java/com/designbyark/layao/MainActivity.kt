package com.designbyark.layao

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.designbyark.layao.ui.cart.CartViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var cartViewModel: CartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        actionBar?.hide()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_category,
                R.id.navigation_user, R.id.navigation_orders
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        cartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        val badgeCount = navView.getOrCreateBadge(R.id.navigation_cart)

        cartViewModel.allCartItems.observe(this, Observer {
            if (it.isEmpty()) {
                badgeCount.isVisible = false
            } else {
                badgeCount.number = it.size
                badgeCount.isVisible = true
            }
        })

    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.general_menu, menu)
//        return true
//    }
}
