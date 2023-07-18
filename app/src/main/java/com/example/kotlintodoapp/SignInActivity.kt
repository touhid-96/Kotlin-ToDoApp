package com.example.kotlintodoapp

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.kotlintodoapp.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignInBinding
    private lateinit var progressDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        //setContentView(R.layout.activity_sign_in)
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        /**
         * Custom Progress Dialog
         */
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(R.layout.layout_progress_dialog)
        progressDialog = dialogBuilder.create()

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
            Toast.makeText(this, "Please type your email!", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Please type your password!", Toast.LENGTH_SHORT).show()
        } else if (email.isEmpty() && password.isEmpty()) {
            Toast.makeText(this, "Please type your email & password!", Toast.LENGTH_SHORT).show()
        }else {
            progressDialog.setCancelable(false)
            showProgressDialog(true)

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    showProgressDialog(false)
                    progressDialog.setCancelable(true)
                    //Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

                    /**
                     * when user sign in and login we need to clear the back trace of signIn activity
                     * purpose : onClick android 'Back' button, user should not be back to the signIn activity again
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

    private fun goToSignUpActivity() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
    }

    private fun showProgressDialog(show: Boolean) {
        if (show) progressDialog.show()
        else progressDialog.dismiss()
    }
}