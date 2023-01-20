package com.jshvarts.todoapp.data

import com.jshvarts.todoapp.data.local.NoteEntity
import com.jshvarts.todoapp.data.local.NotesDao
import com.jshvarts.todoapp.data.local.asNote
import com.jshvarts.todoapp.data.remote.NotesApi
import com.jshvarts.todoapp.data.remote.RemoteNote
import com.jshvarts.todoapp.data.remote.asEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import javax.inject.Inject

class OfflineFirstNoteRepository @Inject constructor(
  val notesApi: NotesApi,
  val notesDao: NotesDao
) : NoteRepository {
  override fun getNotes(): Flow<List<Note>> {
    return notesDao.getNotes().map { entities ->
      entities.map(NoteEntity::asNote)
    }.onEach {
      if (it.isEmpty()) {
        refreshNotes()
      }
    }
  }

  override fun getNote(id: Int): Flow<Note> {
    return notesDao.getNote(id).map { entity ->
      entity.let(NoteEntity::asNote)
    }.onEmpty {
      refreshNotes()
    }
  }

  override suspend fun refreshNotes() {
    notesApi.getNotes()
      .also { remoteNotes ->
        notesDao.insertNotes(remoteNotes.map(RemoteNote::asEntity))
      }
  }
}