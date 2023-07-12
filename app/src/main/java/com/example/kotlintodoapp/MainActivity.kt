package com.example.kotlintodoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

private const val SPLASH_SCREEN_DELAY: Long = 3000 //value is in milliseconds

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_SCREEN_DELAY)
    }
}