package com.example.lab4

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class PowerStateChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val action = intent.action
        if (action == Intent.ACTION_POWER_CONNECTED) {
            Toast.makeText(context, "Power connected", Toast.LENGTH_LONG).show()
        } else if (action == Intent.ACTION_POWER_DISCONNECTED) {
            Toast.makeText(context, "Power disconnected", Toast.LENGTH_LONG).show()
        }
    }
}
