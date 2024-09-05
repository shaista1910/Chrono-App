package com.example.opensource_poe

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CreateTimesheetEntry : AppCompatActivity() {
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance()

    private val userId = firebaseAuth.currentUser?.uid
    private val databaseRef = userId?.let { firebaseDatabase.reference.child("Users").child(it) }
    private val storageRef = userId?.let { firebaseStorage.reference.child("Users").child(it) }

    private lateinit var descText: EditText
    private lateinit var categorySpinner: Spinner
    private var selectedCategory: String? = null
    private var startTime: String = ""
    private var endTime: String = ""
    private var selectedDate: LocalDate? = null
    private var entryId: String? = null
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_PICK = 2
    private lateinit var photoImageView: ImageView
    private var selectedPhoto: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_timesheet_entry)

        descText = findViewById(R.id.descTxtBox)
        categorySpinner = findViewById(R.id.categorySpinner)
        photoImageView = findViewById(R.id.photoImageView)


        // Retrieve the Categories data
        val categoryRef = databaseRef?.child("Categories")
        if (categoryRef != null) {
            categoryRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val categoryNames = ArrayList<String>()
                    for (categorySnapshot in snapshot.children) {
                        val categoryName = categorySnapshot.child("Category Name").getValue(String::class.java)
                        categoryName?.let {
                            categoryNames.add(it)
                        }
                    }

                    // Populating the spinner with the category names
                    val adapter = ArrayAdapter(
                        this@CreateTimesheetEntry,
                        android.R.layout.simple_spinner_item,
                        categoryNames
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    categorySpinner.adapter = adapter

                    categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                            selectedCategory = parent.getItemAtPosition(position) as String
                        }

                        override fun onNothingSelected(parent: AdapterView<*>) {
                            selectedCategory = null
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }// END OF IF STATEMENT FOR SPINNER STUFF

        val startButton: Button = findViewById(R.id.startBttn)
        val endButton: Button = findViewById(R.id.finBttn)
        val dateButton: Button = findViewById(R.id.dateBttn)
        val photoButton: Button = findViewById(R.id.photoBttn)
        val saveButton: Button = findViewById(R.id.save)

        startButton.setOnClickListener {
            showTimePickerDialog(true)
        }

        endButton.setOnClickListener {
            showTimePickerDialog(false)
        }
        dateButton.setOnClickListener {
            showDatePickerDialog()
        }

        photoButton.setOnClickListener {
            showPhotoSelectionDialog()
        }

        saveButton.setOnClickListener {
            if (startTime.isNotEmpty() && endTime.isNotEmpty() && selectedDate != null) {
                val duration = calculateDuration()
                val durationMinutes = duration.toMinutes()
                val dateString = selectedDate!!.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

                val entryData = HashMap<String, Any>()
                entryData["User ID"] = userId!!
                entryData["Description"] = descText.text.toString()
                entryData["Duration"] = durationMinutes
                entryData["Category"] = selectedCategory!!
                entryData["Start Time"] = startTime
                entryData["End Time"] = endTime
                entryData["Date"] = dateString

                val entryRef = databaseRef?.child("Entries")?.push()
                if (entryRef != null) {
                    val entryId = entryRef.key
                    entryId?.let {
                        entryRef.setValue(entryData)
                            .addOnSuccessListener {
                                // Check if a photo is selected
                                if (selectedPhoto != null) {
                                    // Upload the photo to Firebase Storage
                                    uploadPhotoToStorage(selectedPhoto!!, entryId)
                                }
                                val intent = Intent(this, Home::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Failed to save entry", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            } else {
                Toast.makeText(this, "Please select start time, finish time, and date", Toast.LENGTH_SHORT).show()
            }
        }

        val backButton = findViewById<ImageButton>(R.id.backBttn)

        backButton.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
    }// END OF ON CREATE

    private fun showTimePickerDialog(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _: TimePicker?, hourOfDay: Int, minute: Int ->
                // Format the selected time as "HH:mm"
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val selectedTime = timeFormat.format(Date(0, 0, 0, hourOfDay, minute))

                // Update the appropriate button with the selected time
                if (isStartTime) {
                    startTime = selectedTime
                    val startButton: Button = findViewById(R.id.startBttn)
                    startButton.text = startTime
                } else {
                    endTime = selectedTime
                    val endButton: Button = findViewById(R.id.finBttn)
                    endButton.text = endTime
                }

                // Calculate the duration between the start and end times
                calculateDuration()
            },
            currentHour,
            currentMinute,
            true
        )

        timePickerDialog.show()
    }

    private fun calculateDuration(): Duration {
        if (startTime.isNotEmpty() && endTime.isNotEmpty()) {
            // Parse the start and end times
            val startLocalTime = LocalTime.parse(startTime)
            val endLocalTime = LocalTime.parse(endTime)

            // Calculate the duration between the start and end times
            val duration = Duration.between(startLocalTime, endLocalTime)


            val hours = duration.toHours()
            val minutes = duration.toMinutes() % 60

            // Return the calculated duration
            return duration
        }

        // Return an empty duration if either start or end time is missing
        return Duration.ZERO
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            },
            currentYear,
            currentMonth,
            currentDay
        )

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    selectedPhoto = imageBitmap
                    photoImageView.setImageBitmap(selectedPhoto)

                    // Upload the photo to Firebase Storage
                    val entryId = entryId
                    if (entryId != null) {
                        uploadPhotoToStorage(selectedPhoto!!, entryId)
                    }
                }
                REQUEST_IMAGE_PICK -> {
                    val imageUri = data?.data
                    try {
                        val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                        selectedPhoto = imageBitmap
                        photoImageView.setImageBitmap(selectedPhoto)

                        // Upload the photo to Firebase Storage
                        val entryId = entryId
                        if (entryId != null) {
                            uploadPhotoToStorage(selectedPhoto!!, entryId)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun uploadPhotoToStorage(photoBitmap: Bitmap, entryId: String) {
        val photoRef = storageRef?.child("Entries")?.child(entryId)?.child("photo.jpg")

        val baos = ByteArrayOutputStream()
        photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val photoData = baos.toByteArray()

        val uploadTask = photoRef?.putBytes(photoData)
        uploadTask?.addOnSuccessListener {
            // Photo upload success
            photoRef.downloadUrl.addOnSuccessListener { uri ->
                val photoUrl = uri.toString()
                // Store the photo URL in the database or use it as needed
                savePhotoUrlToDatabase(photoUrl, entryId)
            }
        }?.addOnFailureListener {
            // Photo upload failed
        }
    }

    private fun savePhotoUrlToDatabase(photoUrl: String, entryId: String) {
        val entryRef = databaseRef?.child("Entries")?.child(entryId)
        entryRef?.child("PhotoUrl")?.setValue(photoUrl)
            ?.addOnSuccessListener {
                // Photo URL saved to the database
            }
            ?.addOnFailureListener {
                // Failed to save photo URL
            }
    }

    private fun showPhotoSelectionDialog() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Photo")
        builder.setItems(options) { dialog, item ->
            when (item) {
                0 -> takePhotoFromCamera()
                1 -> choosePhotoFromGallery()
                2 -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }


    private fun choosePhotoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

}
