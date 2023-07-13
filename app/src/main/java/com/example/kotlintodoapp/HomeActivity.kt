package com.example.kotlintodoapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kotlintodoapp.databinding.ActivityHomeBinding
import com.example.kotlintodoapp.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityHomeBinding
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        //setContentView(R.layout.activity_home)
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        //custom progress dialogBox
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(R.layout.progress_dialog)
        dialog = dialogBuilder.create()

        binding.signOutButton.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        auth.signOut()

        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }
}