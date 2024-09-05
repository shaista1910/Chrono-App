package com.example.opensource_poe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Welcome : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        supportActionBar?.hide()

        val buttonLogin: Button = findViewById(R.id.loginBttn)
        buttonLogin.setOnClickListener {
            val intent = Intent(this@Welcome, Login::class.java)
            startActivity(intent)
            finish()
        }
        val buttonRegister: Button = findViewById(R.id.registerBttn)
        buttonRegister.setOnClickListener {
            val intent = Intent(this@Welcome, Register::class.java)
            startActivity(intent)
            finish()
        }
    }
}