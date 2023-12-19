package com.vikasjaiswal.unrealfurniture

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection

class CheckConnectivity : BroadcastReceiver()
{
    private val scope = CoroutineScope(Dispatchers.Main)

    fun isInternetAvailable(): Boolean {
        try {
            val urlc: URLConnection = URL("https://www.google.com/").openConnection()
            (urlc as HttpURLConnection).requestMethod = "HEAD"
            urlc.connectTimeout = 1500
            urlc.connect()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override fun onReceive(context: Context, intent: Intent)
    {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        if (capabilities != null)
        {
            if ((capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) || (capabilities.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI)))
            {
                scope.launch {
                    val result = withContext(Dispatchers.IO) {
                        isInternetAvailable()
                    }

                    if (result)
                    {
                        if (context is OfflineActivity)
                        {
                            (context as OfflineActivity).finish()
                        }
                    }
                    else{
                        val intent = Intent(context, OfflineActivity::class.java)
                        context.startActivity(intent)
                    }
                }
            }
            else
            {
                if (context is OfflineActivity)
                {
                    return
                }
                else
                {
                    val intent = Intent(context, OfflineActivity::class.java)
                    context.startActivity(intent)
                }
            }
        }
        else
        {
            if (context is OfflineActivity)
            {
                return
            }
            else
            {
                val intent = Intent(context, OfflineActivity::class.java)
                context.startActivity(intent)
            }
        }

    }

}