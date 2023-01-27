package com.jshvarts.todoapp.data.remote

class FakeNotesApi : NotesApi {
  private var errorResponse: Throwable? = null
  private var noteList: List<RemoteNote> = emptyList()

  override suspend fun getNotes(): List<RemoteNote> {
    return errorResponse?.let { throw it } ?: noteList
  }

  /**
   * Test-only API
   */
  fun sendNotes(noteList: List<RemoteNote>) {
    this.noteList = noteList
    errorResponse = null
  }

  /**
   * Test-only API
   */
  fun sendNotesError(throwable: Throwable) {
    this.errorResponse = throwable
    this.noteList = emptyList()
  }
}