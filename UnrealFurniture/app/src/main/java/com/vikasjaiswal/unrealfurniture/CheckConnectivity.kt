package com.vikasjaiswal.unrealfurniture

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.*
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class CheckConnectivity : BroadcastReceiver() {
    private val scope = CoroutineScope(Dispatchers.Main)

    // Use a suspending function for network call
    private suspend fun isInternetAvailable(): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val url = URL("https://www.google.com/")
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "HEAD"
                urlConnection.connectTimeout = 1500
                urlConnection.connect()
                urlConnection.responseCode == HttpURLConnection.HTTP_OK
            }
        } catch (e: IOException) {
            false
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        scope.launch {
            val result = capabilities?.let {
                it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            } ?: false

            if (result) {
                if (context is OfflineActivity) {
                    (context as OfflineActivity).finish()
                }
            } else {
                if (context !is OfflineActivity) {
                    val intent = Intent(context, OfflineActivity::class.java)
                    context.startActivity(intent)
                }
            }
        }
    }
}
