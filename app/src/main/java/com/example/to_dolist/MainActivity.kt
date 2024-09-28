package com.example.to_dolist

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_dolist.Adapter.ToDoAdapter
import com.example.to_dolist.Model.ToDoModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class MainActivity : AppCompatActivity(), ToDoAdapter.OnItemClickListener {

    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var tasksAdapter: ToDoAdapter
    private lateinit var addButton: FloatingActionButton
    private lateinit var openButton: Button
    private lateinit var saveButton: Button

    private var taskList: MutableList<ToDoModel> = mutableListOf()
    private var nextTaskId = 0
    private val PICK_FILE_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView)
        tasksRecyclerView.layoutManager = LinearLayoutManager(this)

        tasksAdapter = ToDoAdapter(taskList, this)
        tasksRecyclerView.adapter = tasksAdapter

        addButton = findViewById(R.id.add)
        addButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val input = EditText(this)
            builder.setView(input)
                .setTitle("Добавить новое дело")
                .setPositiveButton("Добавить") { _, _ ->
                    val task = input.text.toString()
                    if (task.isNotEmpty()) {
                        val newTask = ToDoModel(nextTaskId++, task, false)
                        taskList.add(newTask)
                        tasksAdapter.notifyItemInserted(taskList.size - 1)
                    }
                }
                .setNegativeButton("Отмена", null)
            builder.show()
        }

        saveButton = findViewById(R.id.save)
        saveButton.setOnClickListener {
            val name = "TO-DO List"
            val extension = ".json"
            var number = 1
            var fileName = "$name($number)$extension"
            val directory = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            )
            var file = File(directory, fileName)

            while (file.exists()) {
                number++
                fileName = "$name($number)$extension"
                file = File(directory, fileName)
            }

            FileOutputStream(file).use { fos ->
                OutputStreamWriter(fos).use { writer ->
                    writer.write(Gson().toJson(taskList))
                }
            }
        }

        openButton = findViewById(R.id.open)
        openButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "application/json"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(Intent.createChooser(
                intent,
                "Выберите файл"
            ), PICK_FILE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                updateTasks(uri)
            }
        }
    }

    private fun updateTasks(uri: Uri) {
        val json = contentResolver.openInputStream(uri)?.bufferedReader().use { it?.readText() }
        val taskListType = object : com.google.gson.reflect.TypeToken<List<ToDoModel>>() {}.type
        val newTasks: List<ToDoModel> = Gson().fromJson(json, taskListType)

        taskList.clear()
        taskList.addAll(newTasks)
        tasksAdapter.notifyDataSetChanged()
    }

    override fun onItemClick(item: ToDoModel) {
        item.status = !item.status
        tasksAdapter.notifyDataSetChanged()
    }

    override fun onDeleteItem(item: ToDoModel) {
        val position = taskList.indexOf(item)
        taskList.removeAt(position)
        tasksAdapter.notifyItemRemoved(position)
    }

    override fun onEditItem(item: ToDoModel) {
        val builder = AlertDialog.Builder(this)
        val input = EditText(this)
        input.setText(item.task)
        builder.setView(input)
            .setTitle("Редактировать дело")
            .setPositiveButton("Сохранить") { _, _ ->
                val updatedTask = input.text.toString()
                if (updatedTask.isNotEmpty()) {
                    item.task = updatedTask
                    tasksAdapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton("Отмена", null)
        builder.show()
    }
}





