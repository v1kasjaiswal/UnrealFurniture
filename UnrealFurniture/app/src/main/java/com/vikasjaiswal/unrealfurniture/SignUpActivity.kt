package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    lateinit var networkReceiver : CheckConnectivity

    lateinit var nameedit : EditText
    lateinit var emailedit : EditText
    lateinit var pass1edit : EditText
    lateinit var pass2edit : EditText

    var auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)

        nameedit = findViewById(R.id.nameedit)
        emailedit = findViewById(R.id.emailedit)
        pass1edit = findViewById(R.id.pass1edit)
        pass2edit = findViewById(R.id.pass2edit)

        networkReceiver = CheckConnectivity()
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

    fun backToSignIn(view: View) {
        finish()
    }

    fun signUp(view: View) {
        var nametxt = nameedit.text.toString()
        var emailtxt = emailedit.text.toString()
        var pass1txt = pass1edit.text.toString()
        var pass2txt = pass2edit.text.toString()

        if (nametxt.isNotBlank() && Pattern.matches("^(?!\\s)[a-zA-Z\\s]{2,}$", nametxt))
        {
            if (emailtxt.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(emailtxt).matches())
            {
                if (pass1txt.isNotBlank()) {
                    if (pass2txt.isNotBlank()) {
                        if (Pattern.matches(
                                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
                                pass1txt
                            )
                        )
                        {
                            if (pass1txt == pass2txt) {
                                auth.fetchSignInMethodsForEmail(emailtxt)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val result = task.result
                                            val signInMethods = result?.signInMethods

                                            Log.d("Signin Singup Method", signInMethods.toString())

                                            if (signInMethods != null && signInMethods.contains(
                                                    EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD
                                                ) || signInMethods!!.contains(
                                                    GoogleAuthProvider.PROVIDER_ID
                                                )
                                            ) {
                                                emailedit.error = "Email already registered"
                                                emailedit.requestFocus()
                                            } else {
                                                auth.createUserWithEmailAndPassword(
                                                    emailtxt,
                                                    pass1txt
                                                )
                                                    .addOnCompleteListener(this)
                                                    { task ->
                                                        if (task.isSuccessful) {
                                                            val user = auth.currentUser

                                                            user?.sendEmailVerification()
                                                                ?.addOnCompleteListener { task ->
                                                                    if (task.isSuccessful) {
                                                                        finish()
                                                                        Toast.makeText(
                                                                            this@SignUpActivity,
                                                                            "Email sent",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    } else {
                                                                        emailedit.error =
                                                                            "Email not sent"
                                                                        emailedit.requestFocus()
                                                                    }
                                                                }
                                                        } else {
                                                            emailedit.error =
                                                                "Email already in use"
                                                            emailedit.requestFocus()
                                                        }
                                                    }
                                            }
                                        } else {
                                            emailedit.error = "Email already registered"
                                            emailedit.requestFocus()
                                        }
                                    }
                                    .addOnFailureListener {
                                        emailedit.error = "Email already registered"
                                        emailedit.requestFocus()
                                    }
                            } else {
                                pass2edit.error = "Passwords do not match"
                                pass2edit.requestFocus()
                            }
                        } else {
                            pass1edit.error = "Password requirements not met"
                            pass1edit.requestFocus()
                        }
                    } else {
                        pass2edit.error = "Password and confirm password not matching"
                        pass2edit.requestFocus()
                    }
                } else {
                    pass1edit.error = "Password cannot be empty"
                    pass1edit.requestFocus()
                }
            } else {
                emailedit.error = "Invalid email"
                emailedit.requestFocus()
            }
        } else {
            nameedit.error = "Invalid name"
            nameedit.requestFocus()
        }
    }

    fun openSupport(view: View) {
        val intent = Intent(this@SignUpActivity, SupportActivity::class.java)
        startActivity(intent)
    }

    fun openInfo(view: View) {
        val intent = Intent(this@SignUpActivity, InfoActivity::class.java)
        startActivity(intent)
    }
}