package com.priyanshparekh.fairshare.group.balances.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.priyanshparekh.fairshare.R
import com.priyanshparekh.fairshare.group.GroupActivity
import com.priyanshparekh.fairshare.utils.Constants

class MessageService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        getSharedPreferences(Constants.PREF_TOKEN, Context.MODE_PRIVATE).edit().putString(Constants.PrefKeys.KEY_TOKEN, token).apply()
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {
            val title = remoteMessage.data["title"]
            val message = remoteMessage.data["message"]

            sendNotification(title, message)
        }
    }

    private fun sendNotification(title: String?, body: String?) {
        val settingPrefs = getSharedPreferences(Constants.PREF_SETTING, MODE_PRIVATE)
        val notificationAllowed = settingPrefs.getBoolean(Constants.PrefKeys.KEY_NOTIFICATION, false)

        if (!notificationAllowed) return

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "FairShare"

        val channel = NotificationChannel(channelId, "FairShare", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(this, GroupActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.app_logo) // Replace with your icon
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        notificationManager.notify(0, builder.build())
    }
}
