package com.jshvarts.todoapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jshvarts.todoapp.data.Note

@Entity(tableName = "note")
data class NoteEntity(
  @PrimaryKey val id: Int,
  val title: String,
  val completed: Boolean,
  val updateTimestamp: Long? = null
)

fun NoteEntity.asNote() = Note(
  id = id,
  title = title,
  completed = completed
)
