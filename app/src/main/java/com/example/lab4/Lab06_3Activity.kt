package com.example.lab4

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.telephony.SmsManager
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.locks.ReentrantLock

class Lab06_3Activity : AppCompatActivity() {

    private lateinit var reentrancelock: ReentrantLock
    private lateinit var swAutoResponse: Switch
    private lateinit var llButtons: LinearLayout
    private lateinit var btnSafe: Button
    private lateinit var btnMayday: Button
    private lateinit var requesters: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var lvMessages: ListView
    private var broadcastReceiver: BroadcastReceiver? = null
    
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    
    companion object {
        @JvmStatic
        var isRunning: Boolean = false
        
        const val SMS_FORWARD_BROADCAST_RECEIVER = "sms_forward_broadcast_receiver"
        const val SMS_MESSAGE_ADDRESS_KEY = "sms_messages_key"
        private const val AUTO_RESPONSE = "auto_response"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab06_3)
        
        findViewByIds()
        initVariables()
        handleOnClickListenner()
        
        // Check if activity was started with SMS addresses
        intent?.getStringArrayListExtra(SMS_MESSAGE_ADDRESS_KEY)?.let { addresses ->
            android.util.Log.d("Lab06_3Activity", "onCreate: Received ${addresses.size} addresses from Intent")
            processReceiveAddresses(addresses)
        }
    }

    private fun findViewByIds() {
        swAutoResponse = findViewById(R.id.sw_auto_response)
        llButtons = findViewById(R.id.ll_buttons)
        btnSafe = findViewById(R.id.btn_safe)
        btnMayday = findViewById(R.id.btn_mayday)
        lvMessages = findViewById(R.id.lv_messages)
    }

    private fun initVariables() {
        // Khởi tạo giá trị ban đầu cho các biến
        sharedPreferences = getPreferences(MODE_PRIVATE)
        editor = sharedPreferences.edit()
        reentrancelock = ReentrantLock()
        requesters = ArrayList()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, requesters)
        lvMessages.setAdapter(adapter)
        
        // Load auto response setting
        val autoResponse = sharedPreferences.getBoolean(AUTO_RESPONSE, false)
        swAutoResponse.setChecked(autoResponse)
        if (autoResponse) llButtons.setVisibility(View.GONE)
        
        initBroadcastReceiver()
    }

    private fun initBroadcastReceiver() {
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                // Get ArrayList addresses
                val addresses = intent?.getStringArrayListExtra(SMS_MESSAGE_ADDRESS_KEY)
                
                // Process these addresses
                if (addresses != null) {
                    processReceiveAddresses(addresses)
                }
            }
        }
    }

    private fun handleOnClickListenner() {
        btnSafe.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                respond(true)
            }
        })
        
        btnMayday.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                respond(false)
            }
        })
        
        swAutoResponse.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
                if (isChecked) {
                    llButtons.setVisibility(View.GONE)
                } else {
                    llButtons.setVisibility(View.VISIBLE)
                }
                
                // Save auto response setting
                editor.putBoolean(AUTO_RESPONSE, isChecked)
                editor.commit()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        isRunning = true
        
        // Make sure BroadcastReceiver was inited
        if (broadcastReceiver == null) {
            initBroadcastReceiver()
        }
        
        val intentFilter = IntentFilter(SMS_FORWARD_BROADCAST_RECEIVER)
        registerReceiver(broadcastReceiver, intentFilter, RECEIVER_NOT_EXPORTED)
    }

    override fun onStop() {
        super.onStop()
        isRunning = false
        
        // Unregister Receiver
        unregisterReceiver(broadcastReceiver)
    }

    private fun processReceiveAddresses(addresses: ArrayList<String>) {
        android.util.Log.d("Lab06_3Activity", "processReceiveAddresses: Adding ${addresses.size} addresses")
        reentrancelock.lock()
        for (i in 0 until addresses.size) {
            if (!requesters.contains(addresses.get(i))) {
                requesters.add(addresses.get(i))
                android.util.Log.d("Lab06_3Activity", "Added: ${addresses.get(i)}")
            }
        }
        adapter.notifyDataSetChanged()
        reentrancelock.unlock()
        android.util.Log.d("Lab06_3Activity", "Total requesters: ${requesters.size}")
        
        // Check to response automatically
        if (swAutoResponse.isChecked()) {
            respond(true)
        }
    }

    private fun respond(to: String, response: String) {
        reentrancelock.lock()
        requesters.remove(to)
        adapter.notifyDataSetChanged()
        reentrancelock.unlock()
        
        // Send the message
        val sms = SmsManager.getDefault()
        sms.sendTextMessage(to, null, response, null, null)
    }

    private fun respond(ok: Boolean) {
        val okString = getString(R.string.i_am_safe_and_well_worry_not)
        val notOkString = getString(R.string.tell_my_mother_i_love_her)
        val outputString = if (ok) okString else notOkString
        
        val requestersCopy = requesters.clone() as ArrayList<String>
        for (to in requestersCopy) {
            respond(to, outputString)
        }
    }
}