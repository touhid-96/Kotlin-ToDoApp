package com.example.kotlintodoapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.kotlintodoapp.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignInBinding
    private lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        //setContentView(R.layout.activity_sign_in)
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        //custom progress dialogBox
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(R.layout.progress_dialog)
        dialog = dialogBuilder.create()

        binding.signInButton.setOnClickListener {
            signIn()
        }

        binding.gotoSignUpTextview.setOnClickListener {
            goToSignUpActivity()
        }
    }

    private fun signIn() {
        val email = binding.loginMailEdittext.text.toString()
        val password = binding.loginPassEdittext.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(this, "Please write your email!", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(this, "We need your password to log you in", Toast.LENGTH_SHORT).show()
        } else {
            dialog.setCancelable(false)
            showProgressDialog(true)

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    showProgressDialog(false)
                    dialog.setCancelable(true)
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun goToSignUpActivity() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun showProgressDialog(show: Boolean) {
        if (show) dialog.show()
        else dialog.dismiss()
    }
}