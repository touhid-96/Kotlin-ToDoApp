package com.example.kotlintodoapp.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlintodoapp.databinding.LayoutRecyclerViewRowBinding

class ToDoAdapter(private val list: MutableList<ToDoData>) :
RecyclerView.Adapter<ToDoAdapter.toDoViewHolder>(){
    private var listener: ToDoAdapterClicksInterface? = null
    fun setListener(listener: ToDoAdapterClicksInterface) {
        this.listener = listener
    }

    inner class toDoViewHolder(val binding: LayoutRecyclerViewRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): toDoViewHolder {
        val binding = LayoutRecyclerViewRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return toDoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: toDoViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.todoTask.text = this._task

                binding.todoEdit.setOnClickListener {
                    listener?.onEditTaskClick(this)
                }

                binding.todoDelete.setOnClickListener {
                    listener?.onDeleteTaskClick(this)
                }
            }
        }
    }

    interface ToDoAdapterClicksInterface {
        fun onDeleteTaskClick(toDoData: ToDoData)
        fun onEditTaskClick(toDoData: ToDoData)
    }
}