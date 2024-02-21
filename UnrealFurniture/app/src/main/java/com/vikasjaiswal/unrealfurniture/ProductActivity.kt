package com.vikasjaiswal.unrealfurniture

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class ProductActivity : AppCompatActivity() {

    lateinit var goBack : CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.product_activity)

        goBack = findViewById(R.id.goBack)

        goBack.setOnClickListener {
            finish()
        }
    }
}