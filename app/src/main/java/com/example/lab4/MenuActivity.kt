package com.example.lab4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val btnLab06_1 = findViewById<Button>(R.id.btn_lab06_1)
        val btnLab06_2 = findViewById<Button>(R.id.btn_lab06_2)
        val btnLab06_3 = findViewById<Button>(R.id.btn_lab06_3)

        btnLab06_1.setOnClickListener {
            val intent = Intent(this, Lab06_1Activity::class.java)
            startActivity(intent)
        }
        
        btnLab06_2.setOnClickListener {
            val intent = Intent(this, PowerStateChangeActivity::class.java)
            startActivity(intent)
        }

        btnLab06_3.setOnClickListener {
            val intent = Intent(this, Lab06_3Activity::class.java)
            startActivity(intent)
        }
    }
}
