package com.jshvarts.todoapp.data

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeNoteRepository : NoteRepository {
  /**
   * The backing hot flow for notes for testing.
   */
  private val notesFlow: MutableSharedFlow<List<Note>> =
    MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

  /**
   * The backing hot flow for notes for testing.
   */
  private val noteFlow: MutableSharedFlow<Note> =
    MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

  /**
   * Value for testing
   */
  private var refreshNotesResult: Result<Unit> = Result.success(Unit)

  /**
   * Value for testing
   */
  private var deleteNoteResult: Result<Unit> = Result.success(Unit)

  /**
   * Value for testing
   */
  private var addNoteResult: Result<Unit> = Result.success(Unit)

  /**
   * Value for testing
   */
  private var updateNoteResult: Result<Unit> = Result.success(Unit)

  override fun getNotes(): Flow<List<Note>> {
    return notesFlow
  }

  override fun getNote(id: Int): Flow<Note> {
    return noteFlow
  }

  override suspend fun refreshNotes(): Result<Unit> {
    return refreshNotesResult
  }

  override suspend fun deleteNote(id: Int): Result<Unit> {
    return deleteNoteResult
  }

  override suspend fun addNote(title: String): Result<Unit> {
    return addNoteResult
  }

  override suspend fun updateNote(note: Note): Result<Unit> {
    return updateNoteResult
  }

  /**
   * A test-only API to allow controlling the Flow of list of notes from tests.
   */
  fun sendNotes(notes: List<Note>) {
    notesFlow.tryEmit(notes)
  }

  /**
   * A test-only API to allow controlling the Flow of a single note from tests.
   */
  fun sendNote(note: Note) {
    noteFlow.tryEmit(note)
  }

  /**
   * A test-only API.
   */
  fun sendRefreshNotesResult(result: Result<Unit>) {
    refreshNotesResult = result
  }

  /**
   * A test-only API.
   */
  fun sendDeleteNoteResult(result: Result<Unit>) {
    deleteNoteResult = result
  }

  /**
   * A test-only API.
   */
  fun sendAddNoteResult(result: Result<Unit>) {
    addNoteResult = result
  }

  /**
   * A test-only API.
   */
  fun sendUpdateNoteResult(result: Result<Unit>) {
    updateNoteResult = result
  }
}