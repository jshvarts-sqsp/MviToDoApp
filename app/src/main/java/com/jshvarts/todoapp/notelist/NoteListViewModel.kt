package com.jshvarts.todoapp.notelist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jshvarts.todoapp.arch.MviViewModel
import com.jshvarts.todoapp.arch.UiResult
import com.jshvarts.todoapp.arch.asUiResult
import com.jshvarts.todoapp.notelist.data.NoteRepository
import com.jshvarts.todoapp.notelist.ui.NoteListUiAction
import com.jshvarts.todoapp.notelist.ui.NoteListUiEffect
import com.jshvarts.todoapp.notelist.ui.NoteListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SAVED_STATE_HANDLE_KEY = "NoteListViewModel_uiState_Key"

@HiltViewModel
class NoteListViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  private val noteRepository: NoteRepository,
) : MviViewModel<NoteListUiAction, NoteListUiState, NoteListUiEffect>(savedStateHandle) {

  override val initialState: NoteListUiState
    get() = NoteListUiState.Loading

  override val savedStateHandleKey: String
    get() = SAVED_STATE_HANDLE_KEY

  override fun dispatchAction(action: NoteListUiAction) {
    if (isStateInBundle()) return

    when (action) {
      NoteListUiAction.LoadList -> handleLoadList()
    }
  }

  private fun handleLoadList() {
    viewModelScope.launch {
      noteRepository
        .getNotes()
        .asUiResult()
        .onStart { mutableUiEffect.emit(NoteListUiEffect.MessageDataLoading) } // example of emitting an effect
        .collect { result ->
          savedStateHandle[SAVED_STATE_HANDLE_KEY] = when (result) {
            is UiResult.Loading -> NoteListUiState.Loading
            is UiResult.Success -> NoteListUiState.Success(result.data)
            is UiResult.Error -> NoteListUiState.Error(result.exception)
          }
        }
    }
  }
}