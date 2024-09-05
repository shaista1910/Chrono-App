package com.example.opensource_poe

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar
import java.util.Locale

class Home : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var entryAdapter: EntryAdapter
    private lateinit var entryRef: DatabaseReference
    private lateinit var startDateButton: Button
    private lateinit var endDateButton: Button
    private var startDate: String = ""
    private var endDate: String = ""
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        entryRef = database.reference.child("Users")

        val currentUser: FirebaseUser? = auth.currentUser
        userId = currentUser?.uid ?: ""

        startDateButton = findViewById(R.id.startDateButton)
        endDateButton = findViewById(R.id.endDateButton)

        val entryList: RecyclerView = findViewById(R.id.entriesList)
        entryAdapter = EntryAdapter { entry ->
            // Handle item click event
            val intent = Intent(this, EntryDetails::class.java)
            intent.putExtra("CATEGORY", entry.category)
            intent.putExtra("DESCRIPTION", entry.description)
            intent.putExtra("DATE", entry.date)
            intent.putExtra("DURATION", entry.duration)
            startActivity(intent)
        }
        entryList.layoutManager = LinearLayoutManager(this)
        entryList.adapter = entryAdapter

        if (userId != null) {
            val userEntriesRef: DatabaseReference =entryRef.child(userId).child("Entries")
            userEntriesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val entries: MutableList<Entry> = mutableListOf()
                    for (entrySnapshot in snapshot.children) {
                        val categoryName = entrySnapshot.child("Category").getValue(String::class.java)
                        val entryDesc = entrySnapshot.child("Description").getValue(String::class.java)
                        val entryDate = entrySnapshot.child("Date").getValue(String::class.java)
                        val entryDuration = entrySnapshot.child("Duration").getValue(Int::class.java)
                        val entryStartTime = entrySnapshot.child("Start Time").getValue(String::class.java)
                        val entryEndTime = entrySnapshot.child("End Time").getValue(String::class.java)
                        val imageUrl = entrySnapshot.child("PhotoUrl").getValue(String::class.java)

                        if (categoryName != null && entryDesc != null && entryDate != null && entryDuration != null && entryStartTime !=null && entryEndTime != null) {
                            val entry = Entry(categoryName, entryDesc, entryDate, entryDuration, imageUrl, entryStartTime, entryEndTime)
                            entries.add(entry)
                        }
                    }
                    entryAdapter.setEntries(entries)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(baseContext, "User not found.", Toast.LENGTH_SHORT).show()
                }
            })
        }

        val createEntryButton = findViewById<Button>(R.id.createEntryBttn)

        createEntryButton.setOnClickListener {
            val intent = Intent(this, CreateTimesheetEntry::class.java)
            startActivity(intent)
        }

        startDateButton.setOnClickListener {
            showDatePickerDialog(true)
        }

        endDateButton.setOnClickListener {
            showDatePickerDialog(false)
        }

        val filterButton: Button = findViewById(R.id.filterBttn)
        filterButton.setOnClickListener {
            filterEntriesByDates()
        }

        val menuButton = findViewById<ImageButton>(R.id.menuBttn)

        menuButton.setOnClickListener {
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
        }

    }

    private fun showDatePickerDialog(isStartDate: Boolean) {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)

            if (isStartDate) {
                startDate = formattedDate
                startDateButton.text = formattedDate
            } else {
                endDate = formattedDate
                endDateButton.text = formattedDate
            }
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun filterEntriesByDates() {
        val userEntriesRef: DatabaseReference = entryRef.child(userId).child("Entries")
        userEntriesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val entries: MutableList<Entry> = mutableListOf()

                for (entrySnapshot in snapshot.children) {
                    val categoryName = entrySnapshot.child("Category").getValue(String::class.java)
                    val entryDesc = entrySnapshot.child("Description").getValue(String::class.java)
                    val entryDate = entrySnapshot.child("Date").getValue(String::class.java)
                    val entryDuration = entrySnapshot.child("Duration").getValue(Int::class.java)
                    val entryStartTime = entrySnapshot.child("Start Time").getValue(String::class.java)
                    val entryEndTime = entrySnapshot.child("End Time").getValue(String::class.java)
                    val imageUrl = entrySnapshot.child("PhotoUrl").getValue(String::class.java)

                    if (categoryName != null && entryDesc != null && entryDate != null && entryDuration != null) {
                        val entry = Entry(categoryName, entryDesc, entryDate, entryDuration, imageUrl, entryStartTime, entryEndTime)
                        if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                            if (entryDate in startDate..endDate) {
                                entries.add(entry)
                            }
                        } else {
                            entries.add(entry)
                        }
                    }
                }

                entryAdapter.setEntries(entries)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(baseContext, "User not found.", Toast.LENGTH_SHORT).show()
            }
        })
    }


}