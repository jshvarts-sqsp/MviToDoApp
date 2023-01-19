package com.jshvarts.todoapp.notedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jshvarts.todoapp.arch.MviViewModel
import com.jshvarts.todoapp.arch.UiResult
import com.jshvarts.todoapp.arch.asUiResult
import com.jshvarts.todoapp.data.NoteRepository
import com.jshvarts.todoapp.notedetail.ui.NoteDetailUiAction
import com.jshvarts.todoapp.notedetail.ui.NoteDetailUiEffect
import com.jshvarts.todoapp.notedetail.ui.NoteDetailUiState
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SAVED_STATE_HANDLE_KEY = "NoteDetailViewModel_uiState_Key"

class NoteDetailViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  private val noteRepository: NoteRepository
) : MviViewModel<NoteDetailUiAction, NoteDetailUiState, NoteDetailUiEffect>(savedStateHandle) {
  override val initialState: NoteDetailUiState
    get() = NoteDetailUiState.Loading

  override val savedStateHandleKey: String?
    get() = TODO("Not yet implemented")

  override val actionHandler: (NoteDetailUiAction) -> Unit = {
    when (it) {
      is NoteDetailUiAction.LoadNote -> onLoadNote(it)
    }
  }

  private fun onLoadNote(action: NoteDetailUiAction.LoadNote) {
    viewModelScope.launch {
      noteRepository
        .getNote(action.id).asUiResult()
        .collect { result ->
          savedStateHandle[SAVED_STATE_HANDLE_KEY] = when (result) {
            is UiResult.Loading -> NoteDetailUiState.Loading
            is UiResult.Success -> NoteDetailUiState.Success(result.data)
            is UiResult.Error -> NoteDetailUiState.Error(result.exception)
          }
        }
    }
  }
}