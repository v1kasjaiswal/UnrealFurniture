package com.vikasjaiswal.unrealfurniture

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import io.karn.notify.Notify
import io.karn.notify.entities.Payload

class FCMNotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: com.google.firebase.messaging.RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Notify.CHANNEL_DEFAULT_KEY,
                "Default Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        Notify.defaultConfig {
            header {
                color = resources.getColor(R.color.darker)
                icon = R.drawable.myorders
                showTimestamp = true
            }
            alerting(Notify.CHANNEL_DEFAULT_KEY) {
                lightColor = resources.getColor(R.color.darker)
                Log.d("FCM", Notify.IMPORTANCE_MAX.toString())
                channelImportance = Notify.IMPORTANCE_MAX
            }
        }

        Notify.with(applicationContext)
            .meta {
                clickIntent = PendingIntent.getActivity(
                    applicationContext,
                    0,
                    Intent(applicationContext, SplashActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            }
            .asBigText {
                title = remoteMessage.data["title"].toString()
                bigText = "Best Regards from Unreal Furniture."
                expandedText = remoteMessage.data["body"].toString()
            }
            .show()

    }
}

