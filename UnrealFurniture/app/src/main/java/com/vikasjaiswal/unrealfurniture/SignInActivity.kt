package com.vikasjaiswal.unrealfurniture

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import kotlin.math.sign

private const val RC_SIGN_IN = 9330

class SignInActivity : AppCompatActivity() {

    lateinit var networkReceiver: CheckConnectivity

    lateinit var signinemail: EditText
    lateinit var signinpass: EditText

    var auth = FirebaseAuth.getInstance()
    lateinit var googleSignInClient: GoogleSignInClient

    var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin_activity)

        networkReceiver = CheckConnectivity()

        signinemail = findViewById(R.id.signinemail)
        signinpass = findViewById(R.id.signinpass)

        val gsio = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestId()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gsio)
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

    fun SignIn(view: View) {
        val emailtxt = signinemail.text.toString()
        val passtxt = signinpass.text.toString()

        if (emailtxt.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(emailtxt).matches()) {
            if (passtxt.isNotBlank() && Pattern.matches(
                    "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\S+\$).{8,}\$",
                    passtxt
                )
            ) {
                auth.fetchSignInMethodsForEmail(emailtxt)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val result = task.result
                            val signInMethods = result?.signInMethods
                            if (signInMethods != null && signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {

                                auth.signInWithEmailAndPassword(emailtxt, passtxt)
                                    .addOnCompleteListener(this) { task ->

                                        var user = auth.currentUser

                                        if (user != null && user.isEmailVerified) {
                                            if (task.isSuccessful) {
                                                val intent = Intent(
                                                    this@SignInActivity,
                                                    MainActivity::class.java
                                                )
                                                startActivity(intent)
                                                finish()
                                            } else {
                                                signinemail.error = "Email or password is incorrect"
                                                signinemail.requestFocus()
                                            }
                                        } else {
                                            signinemail.error = "Email is not verified"
                                            signinemail.requestFocus()
                                        }
                                    }
                                    .addOnFailureListener {
                                        signinemail.error = "Email or Password is incorrect"
                                        signinemail.requestFocus()
                                    }
                            } else {
                                signinemail.error = "Account not found"
                                signinemail.requestFocus()
                            }

                        } else {
                            signinemail.error = "Email is not registered"
                            signinemail.requestFocus()
                        }
                    }
                    .addOnFailureListener {
                        signinemail.error = "Email or password is incorrect"
                        signinemail.requestFocus()
                    }
            } else {
                signinpass.error = "Enter a valid password"
                signinpass.requestFocus()
            }
        } else {
            signinemail.error = "Enter a valid email"
            signinemail.requestFocus()
        }
    }

    fun SignUp(view: View) {
        val intent = Intent(this@SignInActivity, SignUpActivity::class.java)
        startActivity(intent)
    }

    fun SignInWithGoogle(view: View) {
        view.isEnabled = false
        try {
            val signInIntent = googleSignInClient.signInIntent

            startActivityForResult(signInIntent, RC_SIGN_IN)
        } catch (e: Exception) {

            Log.d("SignInActivity", "Error in Sign-in with Google" + e.message)
            Toast.makeText(this, "Error in Sign-in with Google", Toast.LENGTH_SHORT).show()
        } finally {
            view.isEnabled = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            try {
                val account = GoogleSignIn.getLastSignedInAccount(this)

                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

                auth.fetchSignInMethodsForEmail(account!!.email.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val result = task.result
                            val signInMethods = result.signInMethods

                            Log.d("Signin Singup Method", signInMethods.toString())

                            if (signInMethods == null || !signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {

                                auth.signInWithCredential(credential)
                                    .addOnCompleteListener(this) {
                                        if (it.isSuccessful) {
                                            val user = auth.currentUser

                                            if (user != null) {
                                                CoroutineScope(Dispatchers.IO).launch {

                                                val userMap = hashMapOf(
                                                    "useruid" to user.uid,
                                                    "username" to convertToTitleCase(user.displayName.toString()),
                                                    "useremail" to user.email,
                                                )

                                                    try {
                                                        db.collection("users").document(user.uid)
                                                            .set(userMap)
                                                            .addOnSuccessListener {
                                                                runOnUiThread {
                                                                    val intent = Intent(
                                                                        this@SignInActivity,
                                                                        MainActivity::class.java
                                                                    )
                                                                    startActivity(intent)
                                                                    finish()
                                                                }
                                                            }
                                                            .addOnFailureListener {
                                                                // Use runOnUiThread to handle UI updates after the background task
                                                                runOnUiThread {
                                                                    Toast.makeText(
                                                                        this@SignInActivity,
                                                                        "Error in Sign-In With Google",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                }
                                                            }
                                                    } catch (e: Exception) {
                                                        runOnUiThread {
                                                            Toast.makeText(
                                                                this@SignInActivity,
                                                                "Error in Sign-In With Google",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(
                                                    this@SignInActivity,
                                                    "Error in Sign-In With Google",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } else {
                                            Toast.makeText(
                                                this@SignInActivity,
                                                "Error in Sign-In With Google",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(
                                            this@SignInActivity,
                                            "Error in Sign-In With Google",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                Toast.makeText(
                                    this@SignInActivity,
                                    "Account already exists",
                                    Toast.LENGTH_SHORT
                                ).show()
                                googleSignInClient.signOut()
                                auth.signOut()
                            }
                        } else {
                            Toast.makeText(
                                this@SignInActivity,
                                "Error in Sign-In With Google",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .addOnFailureListener {
                        Log.d("SignInActivity", "Error in Sign-in with Google" + it.message)
                    }
            } catch (e: ApiException) {
                Toast.makeText(this, "Error in Sign-in with Google", Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun closeApp(view: View) {
        finish()
    }

    fun convertToTitleCase(input: String): String {
        return input.split(" ").joinToString(" ") { it.toLowerCase().capitalize() }
    }

    fun forgotPassword(view: View) {
        val intent = Intent(this@SignInActivity, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    fun openSupport(view: View) {
        val intent = Intent(this@SignInActivity, SupportActivity::class.java)
        startActivity(intent)
    }

    fun openInfo(view: View) {
        val intent = Intent(this@SignInActivity, InfoActivity::class.java)
        startActivity(intent)
    }
}