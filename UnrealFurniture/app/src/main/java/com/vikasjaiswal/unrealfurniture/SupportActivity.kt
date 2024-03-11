package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class SupportActivity : AppCompatActivity() {

    lateinit var name : EditText
    lateinit var email : EditText
    lateinit var msg : EditText

    lateinit var supportSubmit : Button

    var db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.support_activity)

        name = findViewById(R.id.snameedit)
        email = findViewById(R.id.semailedit)
        msg = findViewById(R.id.smsgedit)

        supportSubmit = findViewById(R.id.supportSubmit)

        supportSubmit.setOnClickListener {
            val nameText = name.text.toString()
            val emailText = email.text.toString()
            val msgText = msg.text.toString()

            if (nameText.isNotEmpty() || emailText.isNotEmpty() || msgText.isNotEmpty()) {
                if (nameText.matches(Regex("^(?!\\s)[a-zA-Z\\s]{2,}$"))) {
                    if (emailText.matches(Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
                        if (msgText.length > 10) {
                            val support = hashMapOf(
                                "name" to nameText,
                                "email" to emailText,
                                "message" to msgText
                            )
                            db.collection("support").add(support)
                                .addOnSuccessListener {
                                    Toast.makeText(this@SupportActivity, "Message Sent Successfully!", Toast.LENGTH_SHORT)
                                        .show()
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this@SupportActivity, "Message Failed to Send!", Toast.LENGTH_SHORT).show()
                                }
                        }
                        else{
                            msg.error = "Message should be more than 10 characters"
                            msg.requestFocus()
                        }
                    }
                    else{
                        email.error = "Invalid Email"
                        email.requestFocus()
                    }
                }
                else{
                    name.error = "Invalid Name"
                    name.requestFocus()
                }
            }
            else{
                Toast.makeText(this@SupportActivity, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun backToPreviousActivity(view: View) {
        finish()
    }

    fun openInfo(view: View) {
        val intent =  Intent(this@SupportActivity, InfoActivity::class.java)
        startActivity(intent)
    }
}