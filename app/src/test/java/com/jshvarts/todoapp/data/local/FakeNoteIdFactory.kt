package com.jshvarts.todoapp.data.local

class FakeNoteIdFactory : NoteIdFactory {

  private var nextIdErrorResponse: Throwable? = null
  private var nextId: Int? = null

  override suspend fun getNextAvailableId(): Int {
    return nextIdErrorResponse?.let { throw it } ?: nextId!!
  }

  /**
   * Test-only API
   */
  fun sendNextAvailableIdError(throwable: Throwable) {
    nextIdErrorResponse = throwable
    nextId = null
  }

  /**
   * Test-only API
   */
  fun sendNextAvailableIdSuccess(id: Int) {
    nextId = id
    nextIdErrorResponse = null
  }
}