package com.jshvarts.todoapp.data.local

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteIdFactoryImpl @Inject constructor(
  val notesDao: NotesDao
) : NoteIdFactory {
  override suspend fun getNextAvailableId(): Int {
    val currentMaxNoteId = notesDao.getCurrentMaxNoteId()
    return currentMaxNoteId + 1
  }
}