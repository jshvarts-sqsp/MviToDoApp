package com.jshvarts.todoapp.data

import com.jshvarts.todoapp.data.local.NoteEntity
import com.jshvarts.todoapp.data.local.NoteIdFactory
import com.jshvarts.todoapp.data.local.NotesDao
import com.jshvarts.todoapp.data.local.asNote
import com.jshvarts.todoapp.data.remote.NotesApi
import com.jshvarts.todoapp.data.remote.RemoteNote
import com.jshvarts.todoapp.data.remote.asEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class OfflineFirstNoteRepository @Inject constructor(
  private val notesApi: NotesApi,
  private val notesDao: NotesDao,
  private val noteIdFactory: NoteIdFactory
) : NoteRepository {
  override fun getNotes(): Flow<List<Note>> {
    return notesDao.getNotesAsFlow().map { entities ->
      entities.map(NoteEntity::asNote)
    }.onEach {
      if (it.isEmpty()) {
        refreshNotes()
      }
    }
  }

  override fun getNote(id: Int): Flow<Note> {
    return notesDao.getNoteAsFlow(id).map { entity ->
      entity.let(NoteEntity::asNote)
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
      notesDao.getNote(id).let {
        notesDao.deleteNote(it)
      }
    }
  }

  override suspend fun addNote(title: String): Result<Unit> {
    return kotlin.runCatching {
      val nextId = noteIdFactory.getNextAvailableId()
      val noteEntity = NoteEntity(
        id = nextId,
        title = title,
        completed = false
      )
      notesDao.insertNote(noteEntity)
    }
  }

  override suspend fun updateNote(note: Note): Result<Unit> {
    return kotlin.runCatching {
      val noteEntity = NoteEntity(
        id = note.id,
        title = note.title,
        completed = note.completed
      )
      // if note with given id exists, it will be updated
      notesDao.insertNote(noteEntity)
    }
  }
}
