package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    lateinit var logoimg : TextView
    lateinit var logoanim : Animation

    lateinit var bottomtxt : TextView

    private val auth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.splash_activity)

        logoimg = findViewById(R.id.logotxt)

        val logoanim = AnimationUtils.loadAnimation(this, R.anim.logoanim)
        logoimg.startAnimation(logoanim)

        bottomtxt = findViewById(R.id.bottomtxt)

        val bottomanim = AnimationUtils.loadAnimation(this, R.anim.bottomanim)
        bottomtxt.startAnimation(bottomanim)

        val gsio = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestId()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gsio)

        val user = auth.currentUser
        val account = GoogleSignIn.getLastSignedInAccount(this)

        Log.d("SplashActivity", "onCreate: ${user?.email}")

        if (user != null && user?.email == "unrealadmin@gmail.com") {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this@SplashActivity, AdminActivity::class.java)
                startActivity(intent)
                finish()
            }, 1500)
        }
        else if (user !=null && user.isEmailVerified || account != null){
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 1500)
        }
        else{
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this@SplashActivity, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }, 1500)

        }
    }
}