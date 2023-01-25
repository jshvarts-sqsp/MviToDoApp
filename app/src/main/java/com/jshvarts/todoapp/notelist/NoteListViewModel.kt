package com.jshvarts.todoapp.notelist

import androidx.lifecycle.viewModelScope
import com.jshvarts.todoapp.arch.EffectProducer
import com.jshvarts.todoapp.arch.MviViewModel
import com.jshvarts.todoapp.arch.Result
import com.jshvarts.todoapp.arch.asResult
import com.jshvarts.todoapp.data.Note
import com.jshvarts.todoapp.data.NoteRepository
import com.jshvarts.todoapp.ui.WhileUiSubscribed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
  private val noteRepository: NoteRepository
) : MviViewModel<NoteListAction, NoteListState>(),
  EffectProducer<NoteListEffect> {

  override val initialState: NoteListState = NoteListState(
    pendingTodosState = NoteListTodosState.Loading,
    completedTodosState = NoteListTodosState.Loading
  )

  override val savedStateHandleKey = null

  override fun dispatchAction(action: NoteListAction) {
    when (action) {
      NoteListAction.PullToRefresh -> onPullToRefresh()
      is NoteListAction.SwipeToDelete -> onDeleteNote(action)
    }
  }

  private val _effect = Channel<NoteListEffect>()
  override val effect: Flow<NoteListEffect> = _effect.receiveAsFlow()

  private val pendingTodos: Flow<Result<List<Note>>> =
    noteRepository.getNotes()
      .map { notes ->
        notes.filter { !it.completed }
      }
      .asResult()

  private val completedTodos: Flow<Result<List<Note>>> =
    noteRepository.getNotes()
      .map { notes ->
        notes.filter { it.completed }
      }
      .asResult()

  override val state: StateFlow<NoteListState> = combine(pendingTodos, completedTodos) { pendingTodosResult, completedTodosResult ->
    val pendingTodosState = when (pendingTodosResult) {
      is Result.Loading -> NoteListTodosState.Loading
      is Result.Success -> NoteListTodosState.Success(pendingTodosResult.data)
      is Result.Error -> NoteListTodosState.Error(pendingTodosResult.exception)
    }

    val completedTodosState = when (completedTodosResult) {
      is Result.Loading -> NoteListTodosState.Loading
      is Result.Success -> NoteListTodosState.Success(completedTodosResult.data)
      is Result.Error -> NoteListTodosState.Error(completedTodosResult.exception)
    }

    NoteListState(
      pendingTodosState = pendingTodosState,
      completedTodosState = completedTodosState
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
          _effect.send(NoteListEffect.RefreshFailed)
        }
    }
  }

  private fun onDeleteNote(action: NoteListAction.SwipeToDelete) {
    viewModelScope.launch {
      noteRepository.deleteNote(action.id)
        .onFailure {
          _effect.send(NoteListEffect.DeleteFailed)
        }
    }
  }
}
