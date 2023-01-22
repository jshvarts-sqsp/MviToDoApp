package com.jshvarts.todoapp.data.remote

import com.jshvarts.todoapp.data.local.NoteEntity
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteNote(
  val id: Int,
  val title: String,
  val completed: Boolean
)

fun RemoteNote.asEntity() = NoteEntity(
  id = id,
  title = title,
  completed = completed,
  updateTimestamp = System.currentTimeMillis()
)