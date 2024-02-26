package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class AdminActivity : AppCompatActivity() {

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
}