package com.jshvarts.todoapp.notedetail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.jshvarts.todoapp.data.FakeNoteRepository
import com.jshvarts.todoapp.data.Note
import com.jshvarts.todoapp.data.NoteValidator
import com.jshvarts.todoapp.util.MainDispatcherRule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private const val TEST_NOTE_ID = 1

class NoteDetailViewModelTest {

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  private val savedStateHandle = SavedStateHandle()

  private val noteRepository = FakeNoteRepository()

  private val noteValidator = NoteValidator()

  private lateinit var viewModel: NoteDetailViewModel

  @Before
  fun setup() {
    viewModel = NoteDetailViewModel(savedStateHandle, noteRepository, noteValidator)
  }

  @Test
  fun `when ViewModel is created, initial state is emitted`() = runTest {
    // THEN
    viewModel.state.test {
      assertEquals(NoteDetailState.Loading, awaitItem())
    }
    assertEquals(viewModel.initialState, viewModel.state.value)

    viewModel.effect.test {
      expectNoEvents()
    }
  }

  @Test
  fun `when LoadNote and repository succeeds, correct states are emitted`() = runTest {
    // GIVEN
    val note = Note(
      id = 1,
      title = "a",
      completed = true
    )

    // WHEN
    viewModel.dispatchAction(NoteDetailAction.LoadNote(TEST_NOTE_ID))

    // THEN
    viewModel.state.test {
      noteRepository.sendNote(note)

      val loadingState = awaitItem()
      assertEquals(NoteDetailState.Loading, loadingState)

      val successState = awaitItem()
      assertEquals(NoteDetailState.Success(note), successState)
    }

    assertEquals(savedStateHandle["NoteDetailViewModel_state_Key"], NoteDetailState.Success(note))

    viewModel.effect.test {
      expectNoEvents()
    }
  }

  @Test
  fun `when EditNote and note is valid, emits correct state with save enabled`() = runTest {
    // GIVEN
    val action = NoteDetailAction.EditNote(
      id = TEST_NOTE_ID,
      title = "a",
      completed = false,
      forEditing = true
    )
    val resultingState = NoteDetailState.EditNote(
      id = action.id,
      title = action.title,
      completed = action.completed,
      saveEnabled = true
    )

    // WHEN
    viewModel.dispatchAction(action)

    // THEN
    viewModel.state.test {
      assertEquals(resultingState, awaitItem())
    }

    assertEquals(savedStateHandle["NoteDetailViewModel_state_Key"], resultingState)

    viewModel.effect.test {
      expectNoEvents()
    }
  }

  @Test
  fun `when EditNote and note is invalid, emits correct state with save disabled`() = runTest {
    // GIVEN
    val action = NoteDetailAction.EditNote(
      id = TEST_NOTE_ID,
      title = "", // empty title makes this note invalid
      completed = false,
      forEditing = true
    )
    val resultingState = NoteDetailState.EditNote(
      id = action.id,
      title = action.title,
      completed = action.completed,
      saveEnabled = false
    )

    // WHEN
    viewModel.dispatchAction(action)

    // THEN
    viewModel.state.test {
      assertEquals(resultingState, awaitItem())
    }

    assertEquals(savedStateHandle["NoteDetailViewModel_state_Key"], resultingState)

    viewModel.effect.test {
      expectNoEvents()
    }
  }

  @Test
  fun `when SaveNote and repository succeeds, correct effect is emitted`() = runTest {
    // GIVEN
    val action = NoteDetailAction.SaveNote(
      id = TEST_NOTE_ID,
      title = "a",
      completed = false
    )

    // WHEN
    noteRepository.sendUpdateNoteResult(Result.success(Unit))
    viewModel.dispatchAction(action)

    // THEN
    viewModel.effect.test {
      assertEquals(NoteDetailEffect.EditSuccess, awaitItem())
    }

    viewModel.state.test {
      awaitItem() // skip initial state
      expectNoEvents()
    }
  }

  @Test
  fun `when SaveNote and repository fails, correct effect is emitted`() = runTest {
    // GIVEN
    val action = NoteDetailAction.SaveNote(
      id = TEST_NOTE_ID,
      title = "a",
      completed = false
    )

    // WHEN
    noteRepository.sendUpdateNoteResult(Result.failure(Throwable()))
    viewModel.dispatchAction(action)

    // THEN
    viewModel.effect.test {
      assertEquals(NoteDetailEffect.EditFailure, awaitItem())
    }

    viewModel.state.test {
      awaitItem() // skip initial state
      expectNoEvents()
    }
  }

  @Test
  fun `when DeleteNote and repository succeeds, correct effect is emitted`() = runTest {
    // WHEN
    noteRepository.sendDeleteNoteResult(Result.success(Unit))
    viewModel.dispatchAction(NoteDetailAction.DeleteNote(TEST_NOTE_ID))

    // THEN
    viewModel.effect.test {
      assertEquals(NoteDetailEffect.DeleteSuccess, awaitItem())
    }

    viewModel.state.test {
      awaitItem() // skip initial state
      expectNoEvents()
    }
  }

  @Test
  fun `when DeleteNote and repository fails, correct effect is emitted`() = runTest {
    // WHEN
    noteRepository.sendDeleteNoteResult(Result.failure(Throwable()))
    viewModel.dispatchAction(NoteDetailAction.DeleteNote(TEST_NOTE_ID))

    // THEN
    viewModel.effect.test {
      assertEquals(NoteDetailEffect.DeleteFailure, awaitItem())
    }

    viewModel.state.test {
      awaitItem() // skip initial state
      expectNoEvents()
    }
  }
}