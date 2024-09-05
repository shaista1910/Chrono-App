package com.example.opensource_poe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class addCategory : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var categoriesRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        val nameEditText = findViewById<TextView>(R.id.categoryTxtBox)
        val firebaseAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()
        categoriesRef = database.reference.child("Users")

        //CHANGES TEXT WHEN SEEK BAR IS MOVED
        val mintxt = findViewById<TextView>(R.id.MinTxt)
        val minSeekBar = findViewById<SeekBar>(R.id.minSeekBar)

        minSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                mintxt.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Not needed for this example, but you can add any necessary actions here
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Not needed for this example, but you can add any necessary actions here
            }
        })

        //CHANGES TEXT WHEN SEEK BAR IS MOVED
        val maxtxt = findViewById<TextView>(R.id.MaxTxt)
        val maxSeekBar = findViewById<SeekBar>(R.id.maxSeekBar)

        maxSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                maxtxt.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Not needed for this example, but you can add any necessary actions here
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Not needed for this example, but you can add any necessary actions here
            }
        })

        val createButton = findViewById<Button>(R.id.createBttn)

        createButton.setOnClickListener {
            val categoryName = (findViewById<EditText>(R.id.categoryTxtBox)).text.toString()
            val minText = (findViewById<TextView>(R.id.MinTxt)).text.toString()
            val maxText = (findViewById<TextView>(R.id.MaxTxt)).text.toString()

            if (categoryName.isNotEmpty() && minText.isNotEmpty() && maxText.isNotEmpty()) {
                val min = minText.toInt()
                val max = maxText.toInt()

                //ADD DATA TO DB
                if (min < max) {
                    val userId = firebaseAuth.currentUser?.uid
                    if (userId != null) {
                        val newCategory = hashMapOf(
                            "Category Name" to categoryName,
                            "Minimum Hours" to min,
                            "Maximum Hours" to max
                        )

                        categoriesRef.child(userId).child("Categories").push().setValue(newCategory)
                            .addOnSuccessListener {
                                val intent = Intent(this, CreateTimesheetEntry::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { error ->
                                Toast.makeText(this, "Failed to add category: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }else {
                    Toast.makeText(this, "Minimum hours cannot be more than maximum hours", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}