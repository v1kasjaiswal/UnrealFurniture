package com.vikasjaiswal.unrealfurniture

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class SupportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.support_activity)
    }

    fun backToPreviousActivity(view: View) {
        finish()
    }
}