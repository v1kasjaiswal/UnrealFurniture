package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var femailedit : EditText
    var auth = FirebaseAuth.getInstance()

    lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgotpassword_activity)



        val gsio = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestId()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this@ForgotPasswordActivity, gsio)
    }

    fun resetPassword(view: View) {
        femailedit = findViewById(R.id.femailedit)

        var femailtxt = femailedit.text.toString()

        if (femailtxt.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(femailtxt).matches())
        {
            auth.fetchSignInMethodsForEmail(femailtxt)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful)
                    {
                        val result = task.result
                        val signInMethods = result?.signInMethods

                        Log.d("SWASD", signInMethods.toString())
                        Log.d("Exception", task.exception.toString())

                        if (signInMethods != null && signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD))
                        {
                            auth.sendPasswordResetEmail(femailtxt)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful)
                                    {
                                        try {
                                            auth.signOut()
                                            googleSignInClient.signOut()

                                            val intent = Intent(this@ForgotPasswordActivity, SignInActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        } catch (e: Exception) {
                                            Toast.makeText(this@ForgotPasswordActivity, "Password reset email sent successfully", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    else
                                    {
                                        femailedit.error = "Email is not registered"
                                        femailedit.requestFocus()
                                    }
                                }
                        }
                        else
                        {
                            femailedit.error = "Email is not registered"
                            Log.d("Exception", task.exception.toString())
                            femailedit.requestFocus()
                        }
                    }
                    else
                    {
                        femailedit.error = "Email is not registered"
                        femailedit.requestFocus()
                    }
                }
        }
        else
        {
            femailedit.error = "Please enter a valid Email"
            femailedit.requestFocus()
        }
    }

    fun backToSignIn(view: View) {
        finish()
    }

    fun openSupport(view: View) {
        val intent = Intent(this@ForgotPasswordActivity, SupportActivity::class.java)
        startActivity(intent)
    }

    fun openInfo(view: View) {
        val intent = Intent(this@ForgotPasswordActivity, InfoActivity::class.java)
        startActivity(intent)
    }
}