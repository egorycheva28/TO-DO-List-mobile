package com.example.to_dolist

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import retrofit2.HttpException
import java.io.IOException
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), ToDoAdapter.OnItemClickListener {
    private lateinit var apiService: ApiService
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var tasksAdapter: ToDoAdapter
    private lateinit var addButton: FloatingActionButton
    private lateinit var updateButton: Button

    private var taskList: MutableList<ToDoModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tasksRecyclerView = findViewById(R.id.tasksRecyclerView)
        tasksRecyclerView.layoutManager = LinearLayoutManager(this)

        tasksAdapter = ToDoAdapter(taskList, this)
        tasksRecyclerView.adapter = tasksAdapter

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5186/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        getTasks()

        addButton = findViewById(R.id.add)
        addButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val input = EditText(this)
            builder.setView(input)
                .setTitle("Добавить новое дело")
                .setPositiveButton("Добавить") { _, _ ->
                    val task = input.text.toString()
                    if (task.isNotEmpty()) {
                        addTask(task)
                    }
                }
                .setNegativeButton("Отмена", null)
            builder.show()
        }

        updateButton = findViewById(R.id.update)
        updateButton.setOnClickListener {
            getTasks()
        }
    }

    override fun onItemClick(item: ToDoModel) {
        if (item.status) {
            incompleteTask(item.id)
        } else {
            completeTask(item.id)
        }
    }

    override fun onDeleteItem(item: ToDoModel) {
        deleteTask(item.id)
    }

    override fun onEditItem(item: ToDoModel) {
        val builder = AlertDialog.Builder(this)
        val input = EditText(this)
        input.setText(item.description)
        builder.setView(input)
            .setTitle("Редактировать дело")
            .setPositiveButton("Сохранить") { _, _ ->
                val updatedTask = input.text.toString()
                if (updatedTask.isNotEmpty()) {
                    editTask(item.id, updatedTask)
                }
            }
            .setNegativeButton("Отмена", null)
        builder.show()
    }

    private fun getTasks() {
        apiService.get().enqueue(object : retrofit2.Callback<List<ToDoModel>> {
            override fun onResponse(
                call: Call<List<ToDoModel>>,
                response: retrofit2.Response<List<ToDoModel>>
            ) {
                if (response.isSuccessful) {
                    taskList.clear()
                    response.body()?.let {
                        it.forEach { task ->
                            println("Task description: ${task.status}")
                        }
                        taskList.addAll(it)
                    }
                    tasksAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<ToDoModel>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun addTask(task: String) {
        apiService.add(task).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    getTasks()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun deleteTask(id: Int) {
        apiService.delete(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    getTasks()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun editTask(id: Int, task: String) {
        apiService.edit(id, task).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    getTasks()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun completeTask(id: Int) {
        apiService.complete(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    getTasks()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun incompleteTask(id: Int) {
        apiService.incomplete(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    getTasks()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}





