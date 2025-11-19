package com.example.lab4

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage

class SmsReceiver : BroadcastReceiver() {
    companion object {
        const val SMS_FORWARD_BROADCAST_RECEIVER = "sms_forward_broadcast_receiver"
        const val SMS_MESSAGE_ADDRESS_KEY = "sms_messages_key"
    }

    override fun onReceive(context: Context, intent: Intent) {
        android.util.Log.e("SMS_RECEIVER", "========== onReceive called ==========")
        android.util.Log.e("SMS_RECEIVER", "Action: ${intent.action}")
        val queryString = "are you ok?"
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            android.util.Log.e("SMS_RECEIVER", "Bundle found with keys: ${bundle.keySet()}")
            val pdusObj = bundle.get("pdus") as? Array<Any>
            if (pdusObj != null) {
                android.util.Log.e("SMS_RECEIVER", "PDUs found: ${pdusObj.size} messages")
                val messages = ArrayList<SmsMessage>()
                val format = bundle.getString("format")
                for (i in pdusObj.indices) {
                    val pdu = pdusObj[i]
                    val msg = if (android.os.Build.VERSION.SDK_INT >= 23) {
                        SmsMessage.createFromPdu(pdu as ByteArray, format)
                    } else {
                        SmsMessage.createFromPdu(pdu as ByteArray)
                    }
                    messages.add(msg)
                }

                val addresses = ArrayList<String>()
                for (msg in messages) {
                    val body = (msg.messageBody ?: "").toLowerCase()
                    android.util.Log.e("SMS_RECEIVER", "SMS from: ${msg.originatingAddress}")
                    android.util.Log.e("SMS_RECEIVER", "SMS body: '$body'")
                    if (body.contains(queryString)) {
                        val addr = msg.originatingAddress ?: ""
                        if (addr.isNotEmpty()) {
                            addresses.add(addr)
                            android.util.Log.e("SMS_RECEIVER", "✓ MATCH FOUND! Added: $addr")
                        }
                    }
                }

                if (addresses.size > 0) {
                    android.util.Log.e("SMS_RECEIVER", "✓ Found ${addresses.size} addresses. Lab06_3Activity.isRunning = ${Lab06_3Activity.isRunning}")
                    // If Lab06_3Activity is not running, start it and pass the addresses
                    if (!Lab06_3Activity.isRunning) {
                        android.util.Log.e("SMS_RECEIVER", "→ TH2: Starting Lab06_3Activity with Intent extras")
                        val iMain = Intent(context, Lab06_3Activity::class.java)
                        iMain.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        iMain.putStringArrayListExtra(SMS_MESSAGE_ADDRESS_KEY, addresses)
                        context.startActivity(iMain)
                    } else {
                        android.util.Log.e("SMS_RECEIVER", "→ TH1: Sending local broadcast to running activity")
                        // Forward addresses to the running Lab06_3Activity via local broadcast
                        val forward = Intent(SMS_FORWARD_BROADCAST_RECEIVER)
                        forward.putStringArrayListExtra(SMS_MESSAGE_ADDRESS_KEY, addresses)
                        context.sendBroadcast(forward)
                    }
                } else {
                    android.util.Log.e("SMS_RECEIVER", "✗ No matching addresses found")
                }
            } else {
                android.util.Log.e("SMS_RECEIVER", "✗ PDUs is null!")
            }
        } else {
            android.util.Log.e("SMS_RECEIVER", "✗ Bundle is null!")
        }
    }
}