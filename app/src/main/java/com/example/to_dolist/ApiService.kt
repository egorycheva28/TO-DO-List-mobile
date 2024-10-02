package com.example.to_dolist

import com.example.to_dolist.Model.ToDoModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("api/todo")
    fun get():
            Call<List<ToDoModel>>

    @POST("api/todo")
    fun add(@Body data: String):
            Call<Void>

    @DELETE("api/todo/{id}")
    fun delete(@Path("id") id: Int):
            Call<Void>

    @PUT("api/todo/{id}")
    fun edit(@Path("id") id: Int, @Body data: String):
            Call<Void>

    @PATCH("api/todo/{id}/complete")
    fun complete(@Path("id") id: Int):
            Call<Void>

    @PATCH("api/todo/{id}/incomplete")
    fun incomplete(@Path("id") id: Int):
            Call<Void>
}