package com.jshvarts.todoapp.notelist

import androidx.lifecycle.viewModelScope
import com.jshvarts.todoapp.arch.MviViewModel
import com.jshvarts.todoapp.arch.UiAction
import com.jshvarts.todoapp.arch.UiResult
import com.jshvarts.todoapp.arch.asUiResult
import com.jshvarts.todoapp.data.Note
import com.jshvarts.todoapp.data.NoteRepository
import com.jshvarts.todoapp.notelist.ui.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
  noteRepository: NoteRepository
) : MviViewModel<UiAction, NoteListUiState>(),
  MviViewModel.EffectProducer<NoteListUiEffect> {

  override val initialState: NoteListUiState = NoteListUiState(
    pendingTodosUiState = NoteListPendingTodosUiState.Loading,
    completedTodosUiState = NoteListCompletedTodosUiState.Loading
  )

  override val savedStateHandleKey = null

  override fun handleAction(action: UiAction) = Unit

  private val _uiEffect = MutableSharedFlow<NoteListUiEffect>()
  override val uiEffect: SharedFlow<NoteListUiEffect> = _uiEffect.asSharedFlow()

  private val pendingTodos: Flow<UiResult<List<Note>>> =
    noteRepository.getNotes(isCompleted = false).asUiResult()

  private val completedTodos: Flow<UiResult<List<Note>>> =
    noteRepository.getNotes(isCompleted = true).asUiResult()

  override val uiState: StateFlow<NoteListUiState> = combine(pendingTodos, completedTodos) { pendingTodosResult, completedTodosResult ->
    val pendingTodosUiState = when (pendingTodosResult) {
      is UiResult.Loading -> NoteListPendingTodosUiState.Loading
      is UiResult.Success -> NoteListPendingTodosUiState.Success(pendingTodosResult.data)
      is UiResult.Error -> NoteListPendingTodosUiState.Error(pendingTodosResult.exception)
    }

    val completedTodosUiState = when (completedTodosResult) {
      is UiResult.Loading -> NoteListCompletedTodosUiState.Loading
      is UiResult.Success -> NoteListCompletedTodosUiState.Success(completedTodosResult.data)
      is UiResult.Error -> NoteListCompletedTodosUiState.Error(completedTodosResult.exception)
    }

    NoteListUiState(
      pendingTodosUiState = pendingTodosUiState,
      completedTodosUiState = completedTodosUiState
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = initialState
  )
}
