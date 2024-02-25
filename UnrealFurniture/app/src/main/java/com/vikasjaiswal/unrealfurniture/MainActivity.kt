package com.vikasjaiswal.unrealfurniture

import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var networkReceiver: CheckConnectivity
    private lateinit var bottomNav: NavigationBarView

    private val PERMISSION_CODE = 1001
    private val PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(
            android.Manifest.permission.POST_NOTIFICATIONS,
            android.Manifest.permission.ACCESS_NOTIFICATION_POLICY
        )
    } else {
        arrayOf(
            android.Manifest.permission.POST_NOTIFICATIONS,
            android.Manifest.permission.ACCESS_NOTIFICATION_POLICY
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        networkReceiver = CheckConnectivity()
        bottomNav = findViewById(R.id.bottomnav)

        setupFragment(HomeFragment())

        bottomNav.setOnItemSelectedListener { handleNavigation(it.itemId) }
        bottomNav.setOnItemReselectedListener { handleNavigation(it.itemId) }

        checkAndRequestPermissions()
    }

    private fun setupFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragment_container, fragment)
            .commitNow()

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
        registerConnectivityReceiver()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(networkReceiver)
    }

    private fun registerConnectivityReceiver() {
        val intentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(networkReceiver, intentFilter)
    }

    private fun checkAndRequestPermissions() {
        if (arePermissionsGranted()) {
            // Permissions are already granted
        } else {
            requestPermissions()
        }
    }

    private fun arePermissionsGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            PERMISSIONS.all {
                ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }
        } else {
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NOTIFICATION_POLICY) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        val permissionsToRequest = PERMISSIONS.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        CoroutineScope(Dispatchers.Main).launch {
            ActivityCompat.requestPermissions(this@MainActivity, permissionsToRequest, PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
        } else {
            Log.d("MainActivity", "Permissions not granted")
        }
    }
}
