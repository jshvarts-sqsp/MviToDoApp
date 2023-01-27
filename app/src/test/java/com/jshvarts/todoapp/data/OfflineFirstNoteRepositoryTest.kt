package com.jshvarts.todoapp.data

import app.cash.turbine.test
import com.jshvarts.todoapp.data.local.FakeNoteIdFactory
import com.jshvarts.todoapp.data.local.FakeNotesDao
import com.jshvarts.todoapp.data.local.NoteEntity
import com.jshvarts.todoapp.data.remote.FakeNotesApi
import com.jshvarts.todoapp.data.remote.RemoteNote
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class OfflineFirstNoteRepositoryTest {
  private val notesApi = FakeNotesApi()

  private val notesDao = FakeNotesDao()

  private val noteIdFactory = FakeNoteIdFactory()

  private lateinit var repository: NoteRepository

  @Before
  fun setup() {
    repository = OfflineFirstNoteRepository(
      notesApi,
      notesDao,
      noteIdFactory
    )
  }

  @Test
  fun `when getNotes and not found in dao, fetch from api`() = runTest {
    // GIVEN
    val remoteNotes = listOf(
      RemoteNote(
        id = 1,
        title = "a",
        completed = false
      ),
      RemoteNote(
        id = 2,
        title = "b",
        completed = false
      )
    )
    val notes = listOf(
      Note(
        id = 1,
        title = "a",
        completed = false
      ),
      Note(
        id = 2,
        title = "b",
        completed = false
      )
    )

    notesDao.sendNotes(emptyList())
    notesApi.sendNotes(remoteNotes)

    // WHEN
    val notesFlow = repository.getNotes()

    // THEN
    notesFlow.test {
      val initialEmission = awaitItem()
      assertEquals(emptyList<Note>(), initialEmission)

      val refreshEmission = awaitItem()
      // ensure that both lists contain the same items regardless of their order
      // (note list emission order is non-deterministic since we re-shuffle when loading from remote)
      assertTrue(notes.containsAll(refreshEmission))
      assertTrue(refreshEmission.containsAll(notes))
    }
  }

  @Test
  fun `when getNotes and found in dao, does not fetch from api`() = runTest {
    // GIVEN
    val noteEntities = listOf(
      NoteEntity(
        id = 1,
        title = "c",
        completed = false
      ),
      NoteEntity(
        id = 2,
        title = "d",
        completed = false
      )
    )
    val notes = listOf(
      Note(
        id = 1,
        title = "c",
        completed = false
      ),
      Note(
        id = 2,
        title = "d",
        completed = false
      )
    )

    notesDao.sendNotes(noteEntities)

    // WHEN
    val notesFlow = repository.getNotes()

    // THEN
    notesFlow.test {
      val entitiesInDao = awaitItem()
      assertEquals(notes, entitiesInDao)
    }
  }

  @Test
  fun `when getNote, correct item is emitted`() = runTest {
    // GIVEN
    val note = Note(
      id = 1,
      title = "a",
      completed = false
    )
    val noteEntity = NoteEntity(
      id = 1,
      title = "a",
      completed = false
    )

    notesDao.sendNote(noteEntity)

    // WHEN
    val noteFlow = repository.getNote(id = 1)

    // THEN
    noteFlow.test {
      assertEquals(note, awaitItem())
    }
  }

  @Test
  fun `when refreshNotes api success, inserts notes into dao`() = runTest {
    // GIVEN
    val noteEntities = listOf(
      NoteEntity(
        id = 1,
        title = "a",
        completed = false
      ),
      NoteEntity(
        id = 2,
        title = "b",
        completed = true
      )
    )
    val remoteNotes = listOf(
      RemoteNote(
        id = 1,
        title = "a",
        completed = false
      ),
      RemoteNote(
        id = 2,
        title = "b",
        completed = true
      )
    )

    notesApi.sendNotes(remoteNotes)

    // WHEN
    val result = repository.refreshNotes()

    // THEN
    assertEquals(Result.success(Unit), result)

    notesDao.getNotesAsFlow().test {
      val emission = awaitItem()
      // ensure that both lists contain the same items regardless of their order
      // (note list emission order is non-deterministic since we re-shuffle when loading from remote)
      // compare ids only since newly inserted entities get assigned timestamps
      assertTrue(noteEntities.map { it.id }.containsAll(emission.map { it.id }))
      assertTrue(emission.map { it.id }.containsAll(noteEntities.map { it.id }))
    }
  }

  @Test
  fun `when refreshNotes api failure, does not insert notes into dao`() = runTest {
    // GIVEN
    val exception = Exception("test")

    notesApi.sendNotesError(exception)

    // WHEN
    val result = repository.refreshNotes()

    // THEN
    assertEquals(Result.failure<Unit>(exception), result)

    notesDao.getNotesAsFlow().test {
      expectNoEvents()
    }
  }

  @Test
  fun `when addNote id factory success, insert note into dao`() = runTest {
    // GIVEN
    val testNoteId = 2
    val noteEntity = NoteEntity(
      id = testNoteId,
      title = "a",
      completed = false
    )
    noteIdFactory.sendNextAvailableIdSuccess(testNoteId)

    // WHEN
    val result = repository.addNote("a")

    // THEN
    assertEquals(Result.success(Unit), result)

    notesDao.getNoteAsFlow(testNoteId).test {
      assertEquals(noteEntity, awaitItem())
    }
  }

  @Test
  fun `when addNote id factory failure, does not insert note into dao`() = runTest {
    // GIVEN
    val exception = Exception()
    val testNoteId = 2
    noteIdFactory.sendNextAvailableIdError(exception)

    // WHEN
    val result = repository.addNote("a")

    // THEN
    assertEquals(Result.failure<Unit>(exception), result)

    notesDao.getNoteAsFlow(testNoteId).test {
      expectNoEvents()
    }
  }
}