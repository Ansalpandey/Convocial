package com.app.convocial.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.preferences.core.edit
import com.app.convocial.MainActivity
import com.app.convocial.R
import com.app.convocial.preferences.UserPreferences
import com.app.convocial.preferences.dataStore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseMessagingService : FirebaseMessagingService() {

  override fun onNewToken(token: String) {
    super.onNewToken(token)
    CoroutineScope(Dispatchers.IO).launch { saveTokenToDataStore(token) }
  }

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    super.onMessageReceived(remoteMessage)

    remoteMessage.data.let { data ->
      val username = data["username"] ?: "Unknown"
      val link = data["link"] ?: "/"

      // Check for Notification Permission
      if (hasNotificationPermission()) {
        showNotification(username, link)
      }
    }
  }

  private suspend fun saveTokenToDataStore(token: String) {
    applicationContext.dataStore.edit { preferences ->
      preferences[UserPreferences.DEVICE_TOKEN] = token
    }
  }

  private fun showNotification(username: String, link: String) {
    val channelId = "default_channel"
    createNotificationChannel(channelId)

    val intent =
      Intent(this, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra("link", link)
      }

    val pendingIntent =
      PendingIntent.getActivity(
        this,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
      )

    val groupKey = "com.example.project_x.LIKES_GROUP"

    // Build individual notification
    val notification =
      NotificationCompat.Builder(this, channelId)
        .setSmallIcon(R.drawable.app_icon)
        .setContentTitle("New Like!")
        .setContentText("$username liked your post.")
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .setGroup(groupKey)
        .build()

    // Create summary notification for the group
    val summaryNotification =
      NotificationCompat.Builder(this, channelId)
        .setContentTitle("New Activity")
        .setContentText("You have new likes.")
        .setSmallIcon(R.drawable.app_icon)
        .setStyle(NotificationCompat.InboxStyle())
        .setGroup(groupKey)
        .setGroupSummary(true)
        .build()

    val notificationManager = NotificationManagerCompat.from(this)
    if (
      ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
        PackageManager.PERMISSION_GRANTED
    ) {
      return
    }
    notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    notificationManager.notify(1, summaryNotification)
  }

  private fun createNotificationChannel(channelId: String) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

      val name = "Like Post Channel"
      val descriptionText = "Channel for like notifications"
      val importance = NotificationManager.IMPORTANCE_HIGH

      val soundUri = Uri.parse("android.resource://" + applicationContext.packageName + "/" + R.raw.notification_sound)

      val audioAttributes =
        AudioAttributes.Builder()
          .setUsage(AudioAttributes.USAGE_NOTIFICATION)
          .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
          .build()

      val channel =
        NotificationChannel(channelId, name, importance).apply {
          description = descriptionText
          setSound(soundUri, audioAttributes)
        }

      try {
        val notificationManager =
          getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
      } catch (e: Exception) {
        Log.e("FCM", "Failed to create notification channel", e)
      }
    }
  }

  private fun hasNotificationPermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED
    } else {
      true
    }
  }
}
