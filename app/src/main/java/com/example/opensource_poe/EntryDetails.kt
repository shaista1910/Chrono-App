package com.example.opensource_poe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class EntryDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_details)

        val categoryTextView: TextView = findViewById(R.id.categoryTextView)
        val descriptionTextView: TextView = findViewById(R.id.descriptionTextView)
        val dateTextView: TextView = findViewById(R.id.dateTextView)
        val durationTextView: TextView = findViewById(R.id.durationTextView)

        // Retrieve the entry details from the intent
        val category = intent.getStringExtra("CATEGORY")
        val description = intent.getStringExtra("DESCRIPTION")
        val date = intent.getStringExtra("DATE")
        val duration = intent.getStringExtra("DURATION")

        // Set the entry details to the text views
        categoryTextView.text = category
        descriptionTextView.text = description
        dateTextView.text = date
        durationTextView.text = duration
    }
}