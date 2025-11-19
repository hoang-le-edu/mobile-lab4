package com.example.lab4

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat

class TestSmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        android.util.Log.e("TEST_SMS", "========== SMS RECEIVED ==========")
        android.util.Log.e("TEST_SMS", "Action: ${intent.action}")
        android.util.Log.e("TEST_SMS", "Extras: ${intent.extras}")
        
        // Show notification instead of Toast (more reliable on Android 9+)
        showNotification(context, "SMS Received!", "Test receiver got SMS broadcast")
        
        // Also try Toast
        Toast.makeText(context, "SMS Broadcast received!", Toast.LENGTH_LONG).show()
    }
    
    private fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "sms_test_channel"
        
        // Create notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "SMS Test Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
        
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(1001, notification)
    }
}
