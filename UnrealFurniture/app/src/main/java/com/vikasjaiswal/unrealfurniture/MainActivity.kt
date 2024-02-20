package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var networkReceiver: CheckConnectivity
    private lateinit var bottomnav: NavigationBarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        networkReceiver = CheckConnectivity()
        bottomnav = findViewById(R.id.bottomnav)

        setupFragment(HomeFragment())

        bottomnav.setOnItemSelectedListener { item ->
            handleNavigation(item.itemId)
        }

        bottomnav.setOnItemReselectedListener { item ->
            handleNavigation(item.itemId)
        }
    }

    private fun setupFragment(fragment: Fragment) {
        CoroutineScope(Dispatchers.Main).launch {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, fragment)
                .commit()
        }
    }

    private fun handleNavigation(itemId: Int): Boolean {
        val fragment = when (itemId) {
            R.id.home -> HomeFragment()
            R.id.search -> SearchFragment()
            R.id.wishlist -> MyWishListFragment()
            R.id.cart -> MyCartFragment()
            R.id.profile -> ProfileFragment()
            else -> return false
        }

        setupFragment(fragment)
        return true
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(networkReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkReceiver)
    }
}
