package com.vikasjaiswal.unrealfurniture

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AR3DActivity : AppCompatActivity() {

    lateinit var webView : WebView

    lateinit var goBack : CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ar3d_activity)

        webView = findViewById(R.id.webView)

        var url = intent.getStringExtra("ModelUrl")

        if (url != null) {
            webView.loadUrl(url)
        }

        webView.settings.javaScriptEnabled = true

        webView.webViewClient = WebViewClient()

        webView.settings.setSupportZoom(true)

        goBack = findViewById(R.id.goBack)

        goBack.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            finish()
        }
        super.onBackPressed()
    }

}