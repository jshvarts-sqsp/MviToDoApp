package com.jshvarts.todoapp.notedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jshvarts.todoapp.arch.*
import com.jshvarts.todoapp.data.Note
import com.jshvarts.todoapp.data.NoteRepository
import com.jshvarts.todoapp.notedetail.ui.NoteDetailUiAction
import com.jshvarts.todoapp.notedetail.ui.NoteDetailUiEffect
import com.jshvarts.todoapp.notedetail.ui.NoteDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SAVED_STATE_HANDLE_KEY = "NoteDetailViewModel_uiState_Key"

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  private val noteRepository: NoteRepository
) : ViewModel(),
  ActionConsumer<NoteDetailUiAction>,
  StateProducer<NoteDetailUiState>,
  EffectProducer<NoteDetailUiEffect> {
  override val initialState: NoteDetailUiState = NoteDetailUiState.Loading

  override val savedStateHandleKey: String = SAVED_STATE_HANDLE_KEY

  override val uiState: StateFlow<NoteDetailUiState> =
    savedStateHandle.getStateFlow(savedStateHandleKey, initialState)

  private val _uiEffect = Channel<NoteDetailUiEffect>()
  override val uiEffect: Flow<NoteDetailUiEffect> = _uiEffect.receiveAsFlow()

  override fun dispatchAction(action: NoteDetailUiAction) {
    when (action) {
      is NoteDetailUiAction.LoadNote -> onLoadNote(action)
      is NoteDetailUiAction.SaveNote -> onSaveNote(action)
      is NoteDetailUiAction.DeleteNote -> onDeleteNote(action)
    }
  }

  private fun onLoadNote(action: NoteDetailUiAction.LoadNote) {
    viewModelScope.launch {
      noteRepository
        .getNote(action.id).asUiResult()
        .collect { result ->
          savedStateHandle[SAVED_STATE_HANDLE_KEY] = when (result) {
            is UiResult.Loading -> NoteDetailUiState.Loading
            is UiResult.Success -> NoteDetailUiState.Success(note = result.data, forEditing = action.forEditing)
            is UiResult.Error -> NoteDetailUiState.Error(result.exception)
          }
        }
    }
  }

  private fun onSaveNote(action: NoteDetailUiAction.SaveNote) {
    viewModelScope.launch {
      val note = Note(
        id = action.id,
        title = action.title,
        completed = action.completed
      )
      noteRepository.updateNote(note)
        .onSuccess {
          savedStateHandle[SAVED_STATE_HANDLE_KEY] = NoteDetailUiState.Success(note = note, forEditing = false)
          _uiEffect.send(NoteDetailUiEffect.EditSuccess)
        }
        .onFailure {
          savedStateHandle[SAVED_STATE_HANDLE_KEY] = NoteDetailUiState.Success(note = note, forEditing = true)
          _uiEffect.send(NoteDetailUiEffect.EditFailure)
        }
    }
  }

  private fun onDeleteNote(action: NoteDetailUiAction.DeleteNote) {
    viewModelScope.launch {
      noteRepository.deleteNote(action.id)
        .onSuccess {
          _uiEffect.send(NoteDetailUiEffect.DeleteSuccess)
        }
        .onFailure {
          _uiEffect.send(NoteDetailUiEffect.DeleteFailure)
        }
    }
  }
}
