package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class AppInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.appinfo_activity)
    }
    
    fun openSupport(view: View) {
        val intent =  Intent(this@AppInfoActivity, SupportActivity::class.java)
        startActivity(intent)
    }
}