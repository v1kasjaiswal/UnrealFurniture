package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var networkReceiver: CheckConnectivity

    lateinit var bottomnav : BottomNavigationView

    var auth = FirebaseAuth.getInstance()
    lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val gsio = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestId()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gsio)

        networkReceiver = CheckConnectivity()

        bottomnav = findViewById(R.id.bottomnav)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit();

        bottomnav.setOnItemSelectedListener {item ->
            when(item.itemId) {
                R.id.home -> {
                    // Replace the existing fragment with HomeFragment and add animations
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slidein_right, R.anim.slideout_left)
                        .replace(R.id.fragment_container, HomeFragment())
                        .commit();

                    true
                }
                R.id.search -> {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slidein_right, R.anim.slideout_left)
                        .replace(R.id.fragment_container, SearchFragment())
                        .commit();
                    true
                }
                R.id.wishlist -> {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slidein_right, R.anim.slideout_left)
                        .replace(R.id.fragment_container, WishListFragment())
                        .commit();
                    true
                }
                R.id.cart -> {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slidein_right, R.anim.slideout_left)
                        .replace(R.id.fragment_container, CartFragment())
                        .commit();
                    true
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.slidein_right, R.anim.slideout_left)
                        .replace(R.id.fragment_container, ProfileFragment())
                        .commit();
                    true
                }
                else -> false
            }
        }
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

    fun signOut(view: View) {
        try {
            auth.signOut()
            googleSignInClient.signOut()

            val intent = Intent(this@MainActivity, SignInActivity::class.java)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Toast.makeText(this@MainActivity, "Something went wrong!", Toast.LENGTH_SHORT).show()
            Log.d("MainActivity", "Error: " + e.message)
        }
    }
}