package com.example.opensource_poe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()


        val logo = findViewById<ImageView>(R.id.logoImgView)
        logo.alpha = 0f
        val app = findViewById<ImageView>(R.id.appImgView)
        app.alpha = 0f
        val slogan = findViewById<ImageView>(R.id.sloganImgView)
        slogan.alpha = 0f
        logo.animate().setDuration(1500).alpha(1f).withEndAction{
            app.animate().setDuration(1500).alpha(1f).withEndAction{
                slogan.animate().setDuration(1500).alpha(1f).withEndAction{
                    Handler().postDelayed({
                        val intent = Intent(this@Splash, Welcome::class.java)
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                        finish()
                    }, 3000)
                }
            }
        }
    }
}