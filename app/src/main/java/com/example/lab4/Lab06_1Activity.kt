package com.example.lab4

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsMessage
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class Lab06_1Activity : AppCompatActivity() {
    private lateinit var tvContent: TextView
    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var intentFilter: IntentFilter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab06_1)
        
        tvContent = findViewById(R.id.tv_content)
        tvContent.text = "Đang chờ tin nhắn mới...\n\nỨng dụng đã đăng ký BroadcastReceiver bằng code để lắng nghe tin nhắn."
        
        initBroadcastReceiver()
    }
    
    private fun initBroadcastReceiver() {
        // Tạo IntentFilter để lắng nghe tin nhắn mới
        intentFilter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        
        // Tạo BroadcastReceiver để xử lý khi có tin nhắn đến
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                android.util.Log.e("LAB06_1", "========== SMS RECEIVED (Code-based Receiver) ==========")
                processReceiveSms(context, intent)
            }
        }
    }
    
    private fun processReceiveSms(context: Context, intent: Intent) {
        val bundle = intent.extras
        if (bundle != null) {
            val pdus = bundle.get("pdus") as? Array<Any>
            if (pdus != null) {
                val messages = ArrayList<SmsMessage>()
                val format = bundle.getString("format")
                
                for (pdu in pdus) {
                    val msg = if (Build.VERSION.SDK_INT >= 23) {
                        SmsMessage.createFromPdu(pdu as ByteArray, format)
                    } else {
                        @Suppress("DEPRECATION")
                        SmsMessage.createFromPdu(pdu as ByteArray)
                    }
                    messages.add(msg)
                }
                
                // Hiển thị thông tin tin nhắn
                for (msg in messages) {
                    val msgBody = msg.messageBody ?: ""
                    val address = msg.originatingAddress ?: ""
                    
                    android.util.Log.e("LAB06_1", "From: $address")
                    android.util.Log.e("LAB06_1", "Body: $msgBody")
                    
                    // Hiển thị Toast
                    val toastMessage = "Tin nhắn mới!\nTừ: $address\nNội dung: $msgBody"
                    Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show()
                    
                    // Cập nhật TextView
                    runOnUiThread {
                        val currentText = tvContent.text.toString()
                        val newText = "✉ TIN NHẮN MỚI\n" +
                                "Từ: $address\n" +
                                "Nội dung: $msgBody\n" +
                                "━━━━━━━━━━━━━━━━\n\n" +
                                currentText
                        tvContent.text = newText
                    }
                }
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Đăng ký BroadcastReceiver khi Activity Resume
        android.util.Log.e("LAB06_1", "Registering BroadcastReceiver (code-based)")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(broadcastReceiver, intentFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(broadcastReceiver, intentFilter)
        }
        Toast.makeText(this, "BroadcastReceiver đã được đăng ký", Toast.LENGTH_SHORT).show()
    }
    
    override fun onStop() {
        super.onStop()
        // Hủy đăng ký BroadcastReceiver khi Activity Stop
        android.util.Log.e("LAB06_1", "Unregistering BroadcastReceiver")
        unregisterReceiver(broadcastReceiver)
        Toast.makeText(this, "BroadcastReceiver đã được hủy đăng ký", Toast.LENGTH_SHORT).show()
    }
}
