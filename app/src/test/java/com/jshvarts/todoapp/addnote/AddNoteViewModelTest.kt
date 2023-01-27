package com.jshvarts.todoapp.addnote

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.jshvarts.todoapp.data.FakeNoteRepository
import com.jshvarts.todoapp.data.NoteValidator
import com.jshvarts.todoapp.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddNoteViewModelTest {

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  private val savedStateHandle = SavedStateHandle()

  private val noteRepository = FakeNoteRepository()

  private val noteValidator = NoteValidator()

  private lateinit var viewModel: AddNoteViewModel

  @Before
  fun setup() {
    viewModel = AddNoteViewModel(savedStateHandle, noteRepository, noteValidator)
  }

  @Test
  fun `when ViewModel is created, initial state is emitted`() = runTest {
    // THEN
    viewModel.state.test {
      assertEquals(viewModel.initialState, awaitItem())
    }

    viewModel.effect.test {
      expectNoEvents()
    }
  }

  @Test
  fun `when WriteNote and validation succeeds, correct state is emitted`() = runTest {
    // GIVEN
    val action = AddNoteAction.WriteNote(title = "a")
    val resultingState = AddNodeState(title = action.title, saveEnabled = true)

    // WHEN
    viewModel.dispatchAction(action)

    // THEN
    viewModel.state.test {
      assertEquals(resultingState, awaitItem())
    }

    assertEquals(savedStateHandle["AddNoteViewModel_state_Key"], resultingState)

    viewModel.effect.test {
      expectNoEvents()
    }
  }

  @Test
  fun `when SaveNote and repository succeeds, correct effect is emitted`() = runTest {
    // GIVEN
    val action = AddNoteAction.SaveNote(title = "a")

    // WHEN
    noteRepository.sendAddNoteResult(Result.success(Unit))
    viewModel.dispatchAction(action)

    // THEN
    viewModel.state.test {
      awaitItem() // skip initial state
      expectNoEvents()
    }

    viewModel.effect.test {
      assertEquals(AddNoteEffect.SaveNoteSuccess, awaitItem())
    }
  }

  @Test
  fun `when SaveNote and repository fails, correct effect is emitted`() = runTest {
    // GIVEN
    val action = AddNoteAction.SaveNote(title = "a")

    // WHEN
    noteRepository.sendAddNoteResult(Result.failure(Throwable()))
    viewModel.dispatchAction(action)

    // THEN
    viewModel.state.test {
      awaitItem() // skip initial state
      expectNoEvents()
    }

    viewModel.effect.test {
      assertEquals(AddNoteEffect.SaveNoteFailure, awaitItem())
    }
  }
}