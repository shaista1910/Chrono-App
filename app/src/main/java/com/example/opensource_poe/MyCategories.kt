package com.example.opensource_poe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

class MyCategories : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categoriesRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_categories)
        // Initialize Firebase
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        categoriesRef = database.reference.child("Users")

        val currentUser: FirebaseUser? = auth.currentUser
        val userId: String? = currentUser?.uid

        val categoryList: RecyclerView = findViewById(R.id.categoryList)
        categoryAdapter = CategoryAdapter()
        categoryList.layoutManager = LinearLayoutManager(this)
        categoryList.adapter = categoryAdapter

        if (userId != null) {
            val userCategoriesRef: DatabaseReference =categoriesRef.child(userId).child("Categories")
            userCategoriesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val categories: MutableList<Category> = mutableListOf()
                    for (categorySnapshot in snapshot.children) {
                        val categoryName = categorySnapshot.child("Category Name").getValue(String::class.java)
                        val minHours = categorySnapshot.child("Minimum Hours").getValue(Int::class.java)
                        val maxHours = categorySnapshot.child("Maximum Hours").getValue(Int::class.java)
                        if (categoryName != null && minHours != null && maxHours != null) {
                            val category = Category(categoryName, minHours, maxHours)
                            categories.add(category)
                        }
                    }
                    categoryAdapter.setCategories(categories)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(baseContext, "User not found.", Toast.LENGTH_SHORT).show()
                }
            })
        }


    }

    fun onDataChange(snapshot: DataSnapshot) {
        val categories: MutableList<Category> = mutableListOf()
        for (categorySnapshot in snapshot.children) {
            val categoryName = categorySnapshot.child("Category Name").getValue(String::class.java)
            val minHours = categorySnapshot.child("Minimum Hours").getValue(Int::class.java)
            val maxHours = categorySnapshot.child("Maximum Hours").getValue(Int::class.java)
            if (categoryName != null && minHours != null && maxHours != null) {
                val category = Category(categoryName, minHours, maxHours)
                categories.add(category)
            }
        }
        categoryAdapter.setCategories(categories)
    }

}