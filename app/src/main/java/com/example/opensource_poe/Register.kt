package com.example.opensource_poe

import android.content.ContentValues.TAG
import android.content.Intent
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.example.opensource_poe.databinding.ActivityRegisterBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.tabs.TabLayout.TabGravity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    public lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var client: GoogleSignInClient
    private lateinit var googleButton: ImageButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("Users")

        googleButton = findViewById(R.id.googleBttn)
        val  options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        client = GoogleSignIn.getClient(this,options)
        googleButton.setOnClickListener {
            val intent = client.signInIntent
            startActivityForResult(intent,10001)
        }



        //What happens when the user clicks the register button
        binding.registerBttn.setOnClickListener{
            val myEmail = binding.emailTxtBox.text.toString()
            val myPassword = binding.passwordTxtBox.text.toString()
            val myConfPass = binding.confPassTxtBox.text.toString()
            val passwordRegex = "(?=.*[A-Z])(?=.*[0-9]).{6,}".toRegex()

            if (!Patterns.EMAIL_ADDRESS.matcher(myEmail).matches()) {
                showToast("Please enter a valid email address")
            } else if (myEmail.isEmpty() || myPassword.isEmpty() || myConfPass.isEmpty() ) {
                showToast("Please fill in all fields.")
            } else if (!passwordRegex.matches(myPassword)) {
                showToast("Password must be at least 6 characters long, contain a capital letter, and a numerical value")
            } else if (myPassword != myConfPass) {
                showToast("Passwords do not match")
            } else {
                firebaseAuth.createUserWithEmailAndPassword(myEmail, myPassword).addOnCompleteListener{task ->
                    if (task.isSuccessful){
                        val user = firebaseAuth.currentUser
                        val userID = user?.uid

                        // Save the UID in the Realtime Database
                        saveUidToDatabase(userID)
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val loginButton = findViewById<Button>(R.id.loginBttn)

            loginButton.setOnClickListener {
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            }

        }// ON CLICK LISTENER
    } //ON CREATE

    private fun saveUidToDatabase(userID: String?) {
        if (userID != null) {
            val userNodeRef = usersRef.child(userID)

            userNodeRef.setValue(true)
                .addOnSuccessListener {
                    // UID saved successfully
                }
                .addOnFailureListener { error ->
                    // Handle the error
                }
        } else {
            // Handle the case where the userID is null
        }
    }


    //METHODS
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==10001){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken,null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener{task->
                    if(task.isSuccessful){

                        val i  = Intent(this,Home::class.java)
                        startActivity(i)

                    }else{
                        Toast.makeText(this,task.exception?.message,Toast.LENGTH_SHORT).show()
                    }

                }
        }
    }


    companion object {
        private const val TAG = "MainActivity"
        private const val RC_SIGN_IN = 9001
    }
}