package com.jshvarts.todoapp.notelist

import androidx.lifecycle.viewModelScope
import com.jshvarts.todoapp.arch.MviViewModel
import com.jshvarts.todoapp.arch.UiAction
import com.jshvarts.todoapp.arch.UiResult
import com.jshvarts.todoapp.arch.asUiResult
import com.jshvarts.todoapp.data.Note
import com.jshvarts.todoapp.data.NoteRepository
import com.jshvarts.todoapp.notelist.ui.*
import com.jshvarts.todoapp.ui.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
  noteRepository: NoteRepository
) : MviViewModel<UiAction, NoteListUiState>(),
  MviViewModel.EffectProducer<NoteListUiEffect> {

  override val initialState: NoteListUiState = NoteListUiState(
    pendingTodosUiState = NoteListTodosUiState.Loading,
    completedTodosUiState = NoteListTodosUiState.Loading
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
      is UiResult.Loading -> NoteListTodosUiState.Loading
      is UiResult.Success -> NoteListTodosUiState.Success(pendingTodosResult.data)
      is UiResult.Error -> NoteListTodosUiState.Error(pendingTodosResult.exception)
    }

    val completedTodosUiState = when (completedTodosResult) {
      is UiResult.Loading -> NoteListTodosUiState.Loading
      is UiResult.Success -> NoteListTodosUiState.Success(completedTodosResult.data)
      is UiResult.Error -> NoteListTodosUiState.Error(completedTodosResult.exception)
    }

    NoteListUiState(
      pendingTodosUiState = pendingTodosUiState,
      completedTodosUiState = completedTodosUiState
    )
  }.stateIn(
    scope = viewModelScope,
    started = WhileUiSubscribed,
    initialValue = initialState
  )
}
