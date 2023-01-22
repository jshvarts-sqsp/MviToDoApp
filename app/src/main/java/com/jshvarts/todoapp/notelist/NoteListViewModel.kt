package com.jshvarts.todoapp.notelist

import androidx.lifecycle.viewModelScope
import com.jshvarts.todoapp.arch.MviViewModel
import com.jshvarts.todoapp.arch.UiResult
import com.jshvarts.todoapp.arch.asUiResult
import com.jshvarts.todoapp.data.Note
import com.jshvarts.todoapp.data.NoteRepository
import com.jshvarts.todoapp.notelist.ui.NoteListTodosUiState
import com.jshvarts.todoapp.notelist.ui.NoteListUiAction
import com.jshvarts.todoapp.notelist.ui.NoteListUiEffect
import com.jshvarts.todoapp.notelist.ui.NoteListUiState
import com.jshvarts.todoapp.ui.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
  private val noteRepository: NoteRepository
) : MviViewModel<NoteListUiAction, NoteListUiState>(),
  MviViewModel.EffectProducer<NoteListUiEffect> {

  override val initialState: NoteListUiState = NoteListUiState(
    pendingTodosUiState = NoteListTodosUiState.Loading,
    completedTodosUiState = NoteListTodosUiState.Loading
  )

  override val savedStateHandleKey = null

  override fun handleAction(action: NoteListUiAction) {
    when (action) {
      NoteListUiAction.PullToRefresh -> onPullToRefresh()
      is NoteListUiAction.SwipeToDelete -> onDeleteNote(action)
    }
  }

  private val _uiEffect = MutableSharedFlow<NoteListUiEffect>()
  override val uiEffect: SharedFlow<NoteListUiEffect> = _uiEffect.asSharedFlow()

  private val pendingTodos: Flow<UiResult<List<Note>>> =
    noteRepository.getNotes(isCompleted = false)
      .asUiResult()

  private val completedTodos: Flow<UiResult<List<Note>>> =
    noteRepository.getNotes(isCompleted = true)
      .asUiResult()

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

  private fun onPullToRefresh() {
    viewModelScope.launch {
      noteRepository.refreshNotes()
        .onFailure {
          _uiEffect.emit(NoteListUiEffect.RefreshFailed)
        }
    }
  }

  private fun onDeleteNote(action: NoteListUiAction.SwipeToDelete) {
    viewModelScope.launch {
      noteRepository.deleteNote(action.id)
        .onFailure {
          _uiEffect.emit(NoteListUiEffect.DeleteFailed)
        }
    }
  }
}
