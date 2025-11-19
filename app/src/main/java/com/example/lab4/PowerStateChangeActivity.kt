package com.example.lab4

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PowerStateChangeActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_power_state)

        tvStatus = findViewById(R.id.tv_status)
    }
}
