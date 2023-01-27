package com.jshvarts.todoapp.notelist

import app.cash.turbine.test
import com.jshvarts.todoapp.data.FakeNoteRepository
import com.jshvarts.todoapp.data.Note
import com.jshvarts.todoapp.util.MainDispatcherRule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val TEST_NOTE_ID = 1

class NoteListViewModelTest {

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  private val noteRepository = FakeNoteRepository()

  private lateinit var viewModel: NoteListViewModel

  @Before
  fun setup() {
    viewModel = NoteListViewModel(noteRepository)
  }

  @Test
  fun `when PullToRefresh action is dispatched and repository succeeds, no effect is emitted`() = runTest {
    // GIVEN
    noteRepository.sendRefreshNotesResult(Result.success(Unit))

    // WHEN
    viewModel.dispatchAction(NoteListAction.PullToRefresh)

    // THEN
    viewModel.effect.test {
      expectNoEvents()
    }
    assertEquals(viewModel.initialState, viewModel.state.value)
  }


  @Test
  fun `when PullToRefresh action is dispatched and repository fails, RefreshFailed effect is emitted`() = runTest {
    // GIVEN
    noteRepository.sendRefreshNotesResult(Result.failure(Throwable()))

    // WHEN
    viewModel.dispatchAction(NoteListAction.PullToRefresh)

    // THEN
    viewModel.effect.test {
      assertEquals(NoteListEffect.RefreshFailed, awaitItem())
      ensureAllEventsConsumed()
    }
    assertEquals(viewModel.initialState, viewModel.state.value)
  }

  @Test
  fun `when SwipeToDelete action is dispatched and repository succeeds, no effect is emitted`() = runTest {
    // GIVEN
    noteRepository.sendDeleteNoteResult(Result.success(Unit))

    // WHEN
    viewModel.dispatchAction(NoteListAction.SwipeToDelete(TEST_NOTE_ID))

    // THEN
    viewModel.effect.test {
      expectNoEvents()
    }
    assertEquals(viewModel.initialState, viewModel.state.value)
  }

  @Test
  fun `when SwipeToDelete action is dispatched and repository succeeds, DeleteFailed effect is emitted`() = runTest {
    // GIVEN
    noteRepository.sendDeleteNoteResult(Result.failure(Throwable()))

    // WHEN
    viewModel.dispatchAction(NoteListAction.SwipeToDelete(TEST_NOTE_ID))

    // THEN
    viewModel.effect.test {
      assertEquals(NoteListEffect.DeleteFailed, awaitItem())
      ensureAllEventsConsumed()
    }
    assertEquals(viewModel.initialState, viewModel.state.value)
  }

  @Test
  fun `when initial view model load succeeds, correct NoteListState is emitted`() = runTest {
    // GIVEN
    val note1 = Note(
      id = 1, title = "a", completed = false
    )
    val note2 = Note(
      id = 2, title = "b", completed = false
    )
    val note3 = Note(
      id = 3, title = "c", completed = true
    )

    // THEN
    viewModel.state.test {
      noteRepository.sendNotes(listOf(note1, note2, note3))
      assertEquals(
        NoteListState(
          NoteListTodosState.Loading,
          NoteListTodosState.Loading,
        ), awaitItem()
      )
      assertEquals(
        NoteListState(
          NoteListTodosState.Success(listOf(note1, note2)),
          NoteListTodosState.Success(listOf(note3)),
        ), expectMostRecentItem()
      )
    }
    viewModel.effect.test {
      expectNoEvents()
    }
  }
}