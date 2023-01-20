package com.jshvarts.todoapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface NotesApi {
  @GET("todos")
  suspend fun getNotes(@Query("completed") completed: Boolean): List<RemoteNote>
}