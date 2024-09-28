package com.example.to_dolist.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolist.Model.ToDoModel
import com.example.to_dolist.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ToDoAdapter(
    private val tasks: MutableList<ToDoModel>,
    private val interaction: OnItemClickListener
) : RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(item: ToDoModel)
        fun onDeleteItem(item: ToDoModel)
        fun onEditItem(item: ToDoModel)

    }

    inner class ToDoViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: ToDoModel, listener: OnItemClickListener) {
            view.findViewById<TextView>(R.id.task_text).text = item.task
            view.findViewById<CheckBox>(R.id.task_checkbox).isChecked = item.status

            view.setOnClickListener {
                listener.onItemClick(item)
            }

            view.findViewById<FloatingActionButton>(R.id.delete_button).setOnClickListener {
                listener.onDeleteItem(item)
            }

            view.findViewById<FloatingActionButton>(R.id.edit_button).setOnClickListener {
                listener.onEditItem(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_layout, parent, false)
        return ToDoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        holder.bind(tasks[position], interaction)
    }

    override fun getItemCount(): Int = tasks.size
}