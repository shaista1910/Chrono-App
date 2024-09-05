package com.example.opensource_poe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val auth = FirebaseAuth.getInstance()

        val homeButton = findViewById<Button>(R.id.homeBttn)

        homeButton.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        val myCategoryButton = findViewById<Button>(R.id.myCatBttn)

        myCategoryButton.setOnClickListener {
            val intent = Intent(this, MyCategories::class.java)
            startActivity(intent)
        }

        val categoryButton = findViewById<Button>(R.id.catBttn)

        categoryButton.setOnClickListener {
            val intent = Intent(this, addCategory::class.java)
            startActivity(intent)
        }

        val createEntryButton = findViewById<Button>(R.id.timesheetBttn)

        createEntryButton.setOnClickListener {
            val intent = Intent(this, CreateTimesheetEntry::class.java)
            startActivity(intent)
        }

        val myCategoriesButton = findViewById<Button>(R.id.prodBttn)

        myCategoriesButton.setOnClickListener {
            val intent = Intent(this, MyCategories::class.java)
            startActivity(intent)
        }

        val stopWatchButton = findViewById<Button>(R.id.timerBttn)

        stopWatchButton.setOnClickListener {
            val intent = Intent(this, StopWatch::class.java)
            startActivity(intent)
        }

        val logoutButton = findViewById<Button>(R.id.logoutBttn)

        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

    }
}