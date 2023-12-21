package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.google.android.material.imageview.ShapeableImageView

class InfoActivity : AppCompatActivity() {

    lateinit var goback : ShapeableImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.info_activity)

        goback = findViewById(R.id.goback)

        val gobackanim = AnimationUtils.loadAnimation(this, R.anim.gobackanim)
        goback.startAnimation(gobackanim)
    }

    fun openSupport(view: View) {
        val intent =  Intent(this@InfoActivity, SupportActivity::class.java)
        startActivity(intent)
    }

    fun goBack(view: View) {
        finish()
    }
}