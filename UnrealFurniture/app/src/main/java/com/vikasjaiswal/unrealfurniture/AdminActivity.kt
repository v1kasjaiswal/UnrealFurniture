package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth

class AdminActivity : AppCompatActivity() {

    private lateinit var networkReceiver: CheckConnectivity
    private lateinit var bottomNav: NavigationBarView

    var auth = FirebaseAuth.getInstance()
    lateinit var googleSignInClient: GoogleSignInClient

    lateinit var signOut : CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_activity)

        signOut = findViewById(R.id.signOut)

        val gsio = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestId()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gsio)

        networkReceiver = CheckConnectivity()
        bottomNav = findViewById(R.id.adminbottomnav)

        setupFragment(AnalyticsFragment())

        bottomNav.setOnItemSelectedListener { handleNavigation(it.itemId) }
        bottomNav.setOnItemReselectedListener { handleNavigation(it.itemId) }


        signOut.setOnClickListener {
            try {
                auth.signOut()
                googleSignInClient.signOut()

                val intent = Intent(this@AdminActivity, SignInActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@AdminActivity, "Something went wrong!", Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "Error: " + e.message)
            }
        }
    }

    private fun setupFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragment_container, fragment)
            .commitNow()
    }

    private fun handleNavigation(itemId: Int): Boolean {
        val fragment = when (itemId) {
            R.id.analytics -> AnalyticsFragment()
            R.id.addproduct -> AddProductFragment()
            R.id.transactions -> TransactionsFragment()
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

}