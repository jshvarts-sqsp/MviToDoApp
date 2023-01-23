package com.jshvarts.todoapp.data.local

interface NoteIdFactory {
  suspend fun getNextAvailableId(): Int
}