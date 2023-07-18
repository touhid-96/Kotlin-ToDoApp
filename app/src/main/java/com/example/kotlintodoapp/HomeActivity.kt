package com.example.kotlintodoapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlintodoapp.databinding.ActivityHomeBinding
import com.example.kotlintodoapp.utils.ToDoAdapter
import com.example.kotlintodoapp.utils.ToDoData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeActivity : AppCompatActivity(), ToDoAdapter.ToDoAdapterClicksInterface {

    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var bindingHome: ActivityHomeBinding
    private lateinit var progressDialog: AlertDialog
    private lateinit var popupDialogAddTask: Dialog
    private lateinit var popupDialogEditTask: Dialog

    private lateinit var addTaskCancelButton: ImageView
    private lateinit var addTaskAddButton: Button
    private lateinit var addTaskDetail: EditText

    private lateinit var editTaskCancelButton: ImageView
    private lateinit var editTaskOkButton: Button
    private lateinit var editTaskDetail: EditText

    private lateinit var adapter: ToDoAdapter
    private lateinit var mList: MutableList<ToDoData>

    private lateinit var taskID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingHome = ActivityHomeBinding.inflate(layoutInflater)
        val view = bindingHome.root
        //setContentView(R.layout.activity_home)
        setContentView(view)

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().reference.child("Tasks").child(auth.currentUser?.uid.toString())

        /**
         * Alert Dialog : Progress Bar (circular)
         */
        val progressDialogBuilder = AlertDialog.Builder(this)
        progressDialogBuilder.setView(R.layout.layout_progress_dialog)
        progressDialog = progressDialogBuilder.create()

        /**
         * Popup Dialog : Add To-Do Task
         */
        popupDialogAddTask = Dialog(this)
        popupDialogAddTask.setContentView(R.layout.layout_add_todo_popup_dialog)
        addTaskCancelButton = popupDialogAddTask.findViewById(R.id.popup_cancel_button)
        addTaskAddButton = popupDialogAddTask.findViewById(R.id.popup_add_button)
        addTaskDetail = popupDialogAddTask.findViewById(R.id.popup_task_detail_edittext)

        /**
         * Popup Dialog : Edit To-Do Task
         */
        popupDialogEditTask = Dialog(this)
        popupDialogEditTask.setContentView(R.layout.layout_edit_todo_popup_dialog)
        editTaskCancelButton = popupDialogEditTask.findViewById(R.id.popup_cancel_button)
        editTaskOkButton = popupDialogEditTask.findViewById(R.id.popup_ok_button)
        editTaskDetail = popupDialogEditTask.findViewById(R.id.popup_task_detail_edittext)

        bindingHome.signOutButton.setOnClickListener {
            signOut()
        }

        bindingHome.addNewTaskButton.setOnClickListener {
            popupDialogAddTask.setCancelable(false)
            popupDialogAddTask.show()
        }

        addTaskCancelButton.setOnClickListener {
            popupDialogAddTask.setCancelable(true)
            popupDialogAddTask.dismiss()
        }

        editTaskCancelButton.setOnClickListener {
            popupDialogEditTask.setCancelable(true)
            popupDialogEditTask.dismiss()
        }

        addTaskAddButton.setOnClickListener {
            saveToDoTask()
        }

        editTaskOkButton.setOnClickListener {
            //Toast.makeText(this, "$editTask $taskID", Toast.LENGTH_SHORT).show()
            editToDoTask(taskID)
        }

        bindingHome.mRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        mList = mutableListOf()
        adapter = ToDoAdapter(mList)
        adapter.setListener(this)
        bindingHome.mRecyclerView.adapter = adapter

        /**
         * method : getDataFromFirebase()
         * Purpose :get all tasks from firebase_DB and show when this activity starts
         */
        getDataFromFirebase()
    }

    private fun editToDoTask(taskID: String) {
        val taskDetail = editTaskDetail.text.toString()
        if (taskDetail.isEmpty()) {
            Toast.makeText(this, "Empty task not allowed!", Toast.LENGTH_SHORT).show()
        }
        else {
            popupDialogEditTask.dismiss()
            editTaskDetail.text = null
            progressDialog.setCancelable(false)
            showProgressDialog(true)

            val map = HashMap<String, Any>()
            map[taskID] = taskDetail
            dbRef.updateChildren(map).addOnCompleteListener {
                if (it.isSuccessful) {
                    Thread.sleep(500) //lets delay for a bit -_-

                    showProgressDialog(false)
                    progressDialog.setCancelable(true)

                    Toast.makeText(this, "Task changed!", Toast.LENGTH_SHORT).show()
                }
                else {
                    Thread.sleep(500) //lets delay for a bit -_-

                    showProgressDialog(false)
                    progressDialog.setCancelable(true)

                    Toast.makeText(this, it.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getDataFromFirebase() {
        dbRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for (taskSnapshot in snapshot.children) {
                    val toDoTask = taskSnapshot.key?.let {
                        ToDoData(it, taskSnapshot.value.toString())
                    }

                    if (toDoTask != null) {
                        mList.add(toDoTask)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun saveToDoTask() {
        val todoTask = addTaskDetail.text.toString()
        if (todoTask.isEmpty()) {
            Toast.makeText(this, "Please type any task!", Toast.LENGTH_SHORT).show()
        }
        else {
            popupDialogAddTask.dismiss()
            addTaskDetail.text = null
            progressDialog.setCancelable(false)
            showProgressDialog(true)

            dbRef.push().setValue(todoTask).addOnCompleteListener {
                if (it.isSuccessful) {
                    Thread.sleep(500) //lets delay for a bit -_-

                    showProgressDialog(false)
                    progressDialog.setCancelable(true)

                    Toast.makeText(this, "Successfully Saved!", Toast.LENGTH_SHORT).show()
                }
                else {
                    Thread.sleep(500) //lets delay for a bit -_-

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

    override fun onDeleteTaskClick(toDoData: ToDoData) {
        progressDialog.setCancelable(false)
        showProgressDialog(true)

        dbRef.child(toDoData._taskID).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Thread.sleep(1000) //lets delay for a bit -_-

                showProgressDialog(false)
                progressDialog.setCancelable(true)

                Toast.makeText(this, "Task Deleted!", Toast.LENGTH_SHORT).show()
            }
            else {
                Thread.sleep(1000) //lets delay for a bit -_-

                showProgressDialog(false)
                progressDialog.setCancelable(true)

                Toast.makeText(this, it.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onEditTaskClick(toDoData: ToDoData) {
        /**
         * steps:
         * (1) get the data (task text, taskID)
         * (2) show edit_todo_popup_dialog with the task string on text field
         * (3) get the _taskID of the selected task from ToDoData - because we need the task ID to edit the specific task
         */
        editTaskDetail.setText(toDoData._task)
        popupDialogEditTask.show()

        //editTask = toDoData._task
        taskID = toDoData._taskID
    }
}