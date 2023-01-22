package com.jshvarts.todoapp.data.remote

import retrofit2.http.GET

interface NotesApi {
  @GET("todos")
  suspend fun getNotes(): List<RemoteNote>
}