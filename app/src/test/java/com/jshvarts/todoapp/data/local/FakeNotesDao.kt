package com.jshvarts.todoapp.data.local

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

class FakeNotesDao : NotesDao {
  /**
   * The backing hot flow for notes for testing.
   */
  private val notesFlow: MutableSharedFlow<List<NoteEntity>> =
    MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

  /**
   * The backing hot flow for notes for testing.
   */
  private val noteFlow: MutableSharedFlow<NoteEntity> =
    MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

  private var note: NoteEntity? = null

  private var deleteErrorResponse: Throwable? = null
  private var insertNotesErrorResponse: Throwable? = null
  private var insertNoteErrorResponse: Throwable? = null
  private var getCurrentMaxNoteErrorResponse: Throwable? = null
  private var currentMaxId: Int? = null

  override fun getNotesAsFlow(): Flow<List<NoteEntity>> = notesFlow

  override fun getNoteAsFlow(id: Int): Flow<NoteEntity> = noteFlow

  override suspend fun getNote(id: Int): NoteEntity = note!!

  override suspend fun deleteNote(entity: NoteEntity) {
    return deleteErrorResponse?.let { throw it } ?: Unit
  }

  override suspend fun insertNotes(notes: List<NoteEntity>) {
    return insertNotesErrorResponse?.let { throw it } ?: run {
      notesFlow.tryEmit(notes)
      Unit
    }
  }

  override suspend fun insertNote(note: NoteEntity) {
    return insertNoteErrorResponse?.let { throw it } ?: run {
      noteFlow.tryEmit(note)
      Unit
    }
  }

  override suspend fun getCurrentMaxNoteId(): Int {
    return getCurrentMaxNoteErrorResponse?.let { throw it } ?: currentMaxId!!
  }

  /**
   * Test-only API
   */
  fun sendNotes(noteList: List<NoteEntity>) {
    notesFlow.tryEmit(noteList)
  }

  /**
   * Test-only API
   */
  fun sendNote(note: NoteEntity) {
    noteFlow.tryEmit(note)
  }

  /**
   * Test-only API
   */
  fun sendDeleteNoteError(throwable: Throwable?) {
    deleteErrorResponse = throwable
  }

  /**
   * Test-only API
   */
  fun sendInsertNotesError(throwable: Throwable?) {
    insertNotesErrorResponse = throwable
  }

  /**
   * Test-only API
   */
  fun sendInsertNoteError(throwable: Throwable?) {
    insertNoteErrorResponse = throwable
  }

  /**
   * Test-only API
   */
  fun sendCurrentMaxId(id: Int) {
    this.currentMaxId = id
  }
}