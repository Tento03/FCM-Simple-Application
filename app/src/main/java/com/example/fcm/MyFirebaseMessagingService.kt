package com.example.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("TAG_FCM","Refreshed token:${token}")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (message.notification!=null){
            val title=message.notification?.title
            val body=message.notification?.body
            val messages=message.data["message"]

            Log.d("FCM","Notifikasi diterima $title $body $message")
            showNotification(title,body,messages)
        }
    }

    private fun showNotification(title: String?, body: String?, messages: String?) {
        val channelId="id"
        val notifId=1
        val notificationManager=getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel=NotificationChannel(channelId,"My Channel",NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val intent= packageManager.getLaunchIntentForPackage(packageName)?.apply {
            flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_MUTABLE)
        val notification=NotificationCompat.Builder(this,channelId)
            .setSmallIcon(R.drawable.ic_notif)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(notifId,notification)
    }
}