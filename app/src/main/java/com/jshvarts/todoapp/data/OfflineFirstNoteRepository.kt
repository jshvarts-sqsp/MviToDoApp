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
  private val notesApi: NotesApi,
  private val notesDao: NotesDao
) : NoteRepository {
  override fun getNotes(isCompleted: Boolean): Flow<List<Note>> {
    return notesDao.getNotes(isCompleted).map { entities ->
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

  override suspend fun refreshNotes(): Result<Unit> {
    return kotlin.runCatching {
      notesApi.getNotes()
        .also { remoteNotes ->
          notesDao.insertNotes(
            remoteNotes.map(RemoteNote::asEntity)
              .shuffled()
          )
        }
    }
  }

  override suspend fun deleteNote(id: Int): Result<Unit> {
    return kotlin.runCatching {
      notesDao.getNote(id).collect {
        notesDao.deleteNote(it)
      }
    }
  }
}