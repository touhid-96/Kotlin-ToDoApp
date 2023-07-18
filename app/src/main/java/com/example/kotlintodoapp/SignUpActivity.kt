package com.example.kotlintodoapp

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.kotlintodoapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        //setContentView(R.layout.activity_sign_up)
        setContentView(view)  //for binding

        auth = FirebaseAuth.getInstance()

        /**
         * Custom Progress Dialog
         */
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(R.layout.layout_progress_dialog)
        progressDialog = dialogBuilder.create()

        binding.signUpButton.setOnClickListener {
            signUp()
        }

        binding.gotoSignInTextview.setOnClickListener {
            goToSignInActivity()
        }
    }

    private fun goToSignInActivity() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
    }

    private fun signUp() {
        val email = binding.regMailEdittext.text.toString()
        val password = binding.regPassEdittext.text.toString()
        val confirmPassword = binding.regConfPassEdittext.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(this, "Please give us your email!", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Please type a password!", Toast.LENGTH_SHORT).show()
        } else if (password.length < 6) {
            Toast.makeText(this, "Password must be of 6 digits or more!", Toast.LENGTH_SHORT).show()
        }else if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please confirm the password!", Toast.LENGTH_SHORT).show()
        } else if (password != confirmPassword) {
            Toast.makeText(this, "Password didn't match!", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please type a valid email address!", Toast.LENGTH_SHORT).show()
        } else {
            progressDialog.setCancelable(false)
            showProgressDialog(true)

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    showProgressDialog(false)
                    progressDialog.setCancelable(true)

                    //Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()

                    /**
                     * when user sign up and login we need to clear the back trace of signUp activity
                     * purpose : onClick android 'Back' button, user should not be back to the signUp activity again
                     * rather the 'Back' button should exit the app from the homeActivity
                     */
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    showProgressDialog(false)
                    progressDialog.setCancelable(true)

                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showProgressDialog(show: Boolean) {
        if (show) progressDialog.show()
        else progressDialog.dismiss()
    }
}