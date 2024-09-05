package com.example.opensource_poe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.opensource_poe.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        firebaseAuth = FirebaseAuth.getInstance()

        binding.loginBttn.setOnClickListener{
            val myEmail = binding.emailTxtBox.text.toString()
            val myPassword = binding.passwordTxtBox.text.toString()

            if(myEmail.isNotEmpty() && myPassword.isNotEmpty()){

                firebaseAuth.signInWithEmailAndPassword(myEmail, myPassword)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, navigate to next
                            val intent = Intent(this, Home::class.java)
                            startActivity(intent)
                            Toast.makeText(baseContext, "Authentication Successful.", Toast.LENGTH_SHORT,).show()

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT,).show()

                        }
                    }
                    .addOnFailureListener{
                        Toast.makeText(baseContext, "Authentication Failed. ${it.localizedMessage}", Toast.LENGTH_SHORT)
                            .show()
                    }
            }else{
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        val buttonSignUp: Button = findViewById(R.id.signUpBttn)
        buttonSignUp.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }
}