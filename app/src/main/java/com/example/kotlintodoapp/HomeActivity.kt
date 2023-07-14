package com.example.kotlintodoapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.kotlintodoapp.databinding.ActivityHomeBinding
import com.example.kotlintodoapp.databinding.AddTodoPopupDialogBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.collection.LLRBNode.Color

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var bindingHome: ActivityHomeBinding
    private lateinit var progressDialog: AlertDialog
    private lateinit var popupDialog: Dialog

    private lateinit var popupCancelButton: ImageView
    private lateinit var popupAddButton: Button
    private lateinit var popupTaskDetail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingHome = ActivityHomeBinding.inflate(layoutInflater)
        val view = bindingHome.root
        //setContentView(R.layout.activity_home)
        setContentView(view)

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().reference.child("Tasks").child(auth.currentUser?.uid.toString())

        /**
         * Custom Progress Dialog
         */
        val progressDialogBuilder = AlertDialog.Builder(this)
        progressDialogBuilder.setView(R.layout.progress_dialog)
        progressDialog = progressDialogBuilder.create()

        /**
         * Custom Popup Dialog
         */
        popupDialog = Dialog(this)
        popupDialog.setContentView(R.layout.add_todo_popup_dialog)
        popupCancelButton = popupDialog.findViewById(R.id.popup_cancel_button)
        popupAddButton = popupDialog.findViewById(R.id.popup_add_button)
        popupTaskDetail = popupDialog.findViewById(R.id.popup_task_detail_edittext)

        bindingHome.signOutButton.setOnClickListener {
            signOut()
        }

        bindingHome.addNewTaskButton.setOnClickListener {
            popupDialog.setCancelable(false)
            popupDialog.show()
        }

        popupCancelButton.setOnClickListener {
            popupDialog.setCancelable(true)
            popupDialog.dismiss()
        }

        popupAddButton.setOnClickListener {
            saveToDoTask()
        }
    }

    private fun saveToDoTask() {
        val todoTask = popupTaskDetail.text.toString()
        if (todoTask.isEmpty()) {
            Toast.makeText(this, "Please type any task!", Toast.LENGTH_SHORT).show()
        } else {
            popupDialog.dismiss()
            popupTaskDetail.text = null
            progressDialog.setCancelable(false)
            showProgressDialog(true)



            dbRef.push().setValue(todoTask).addOnCompleteListener {
                if (it.isSuccessful) {
                    Thread.sleep(1000) //lets delay for a bit -_-

                    showProgressDialog(false)
                    progressDialog.setCancelable(true)

                    Toast.makeText(this, "Successfully Saved!", Toast.LENGTH_SHORT).show()
                } else {
                    Thread.sleep(1000) //lets delay for a bit -_-

                    showProgressDialog(false)
                    progressDialog.setCancelable(true)

                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun signOut() {
        auth.signOut()

        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showProgressDialog(show: Boolean) {
        if (show) progressDialog.show()
        else progressDialog.dismiss()
    }
}