package com.jshvarts.todoapp.notedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jshvarts.todoapp.arch.*
import com.jshvarts.todoapp.data.Note
import com.jshvarts.todoapp.data.NoteRepository
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
) : MviViewModel<NoteDetailAction>(),
  StateProducer<NoteDetailState>,
  EffectProducer<NoteDetailEffect> {
  override val initialState: NoteDetailState = NoteDetailState.Loading

  override val savedStateHandleKey: String = SAVED_STATE_HANDLE_KEY

  override val state: StateFlow<NoteDetailState> =
    savedStateHandle.getStateFlow(savedStateHandleKey, initialState)

  private val _effect = Channel<NoteDetailEffect>()
  override val effect: Flow<NoteDetailEffect> = _effect.receiveAsFlow()

  override fun dispatchAction(action: NoteDetailAction) {
    when (action) {
      is NoteDetailAction.LoadNote -> onLoadNote(action)
      is NoteDetailAction.SaveNote -> onSaveNote(action)
      is NoteDetailAction.DeleteNote -> onDeleteNote(action)
    }
  }

  private fun onLoadNote(action: NoteDetailAction.LoadNote) {
    viewModelScope.launch {
      noteRepository
        .getNote(action.id).asResult()
        .collect { result ->
          savedStateHandle[SAVED_STATE_HANDLE_KEY] = when (result) {
            is Result.Loading -> NoteDetailState.Loading
            is Result.Success -> NoteDetailState.Success(note = result.data, forEditing = action.forEditing)
            is Result.Error -> NoteDetailState.Error(result.exception)
          }
        }
    }
  }

  private fun onSaveNote(action: NoteDetailAction.SaveNote) {
    viewModelScope.launch {
      val note = Note(
        id = action.id,
        title = action.title,
        completed = action.completed
      )
      noteRepository.updateNote(note)
        .onSuccess {
          savedStateHandle[SAVED_STATE_HANDLE_KEY] = NoteDetailState.Success(note = note, forEditing = false)
          _effect.send(NoteDetailEffect.EditSuccess)
        }
        .onFailure {
          savedStateHandle[SAVED_STATE_HANDLE_KEY] = NoteDetailState.Success(note = note, forEditing = true)
          _effect.send(NoteDetailEffect.EditFailure)
        }
    }
  }

  private fun onDeleteNote(action: NoteDetailAction.DeleteNote) {
    viewModelScope.launch {
      noteRepository.deleteNote(action.id)
        .onSuccess {
          _effect.send(NoteDetailEffect.DeleteSuccess)
        }
        .onFailure {
          _effect.send(NoteDetailEffect.DeleteFailure)
        }
    }
  }
}
