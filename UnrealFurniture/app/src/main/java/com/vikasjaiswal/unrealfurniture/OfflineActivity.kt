package com.vikasjaiswal.unrealfurniture

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class OfflineActivity : AppCompatActivity() {

    lateinit var networkReceiver : CheckConnectivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.offline_activity)

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

    fun openInfo(view: View) {
        val intent =  Intent(this@OfflineActivity, InfoActivity::class.java)
        startActivity(intent)
    }
}