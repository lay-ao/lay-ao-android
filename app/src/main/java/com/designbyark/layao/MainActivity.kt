package com.designbyark.layao

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.designbyark.layao.databinding.ActivityMainBinding
import com.designbyark.layao.viewmodels.CartViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var cartViewModel: CartViewModel
    private lateinit var appBarConfiguration: AppBarConfiguration
//    private lateinit var firebaseAuth: FirebaseAuth
//    var firebaseUser: FirebaseUser? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

//        firebaseAuth = FirebaseAuth.getInstance()
//        firebaseAuth.addAuthStateListener { firebaseAuth ->
//            if (firebaseAuth.currentUser == null) {
//                Log.d(LOG_TAG, "Not Signed in!")
//            } else {
//                firebaseUser = firebaseAuth.currentUser
//                Log.d(LOG_TAG, "Current user: ${firebaseUser?.email}")
//            }
//        }

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
        });
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.general_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(findNavController(R.id.nav_host_fragment))
                || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp(appBarConfiguration)
    }
}
