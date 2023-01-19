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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SAVED_STATE_HANDLE_KEY = "NoteDetailViewModel_uiState_Key"

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  private val noteRepository: NoteRepository
) : MviViewModel<NoteDetailUiAction, NoteDetailUiState>(savedStateHandle),
  MviViewModel.EffectProducer<NoteDetailUiEffect> {
  override val initialState: NoteDetailUiState = NoteDetailUiState.Loading

  override val savedStateHandleKey: String = SAVED_STATE_HANDLE_KEY

  override val uiState: StateFlow<NoteDetailUiState> =
    savedStateHandle.getStateFlow(savedStateHandleKey, initialState)
  
  private val _uiEffect = MutableSharedFlow<NoteDetailUiEffect>()
  override val uiEffect: SharedFlow<NoteDetailUiEffect> = _uiEffect.asSharedFlow()

  override fun handleAction(action: NoteDetailUiAction) {
    when (action) {
      is NoteDetailUiAction.LoadNote -> onLoadNote(action)
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