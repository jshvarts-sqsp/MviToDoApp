package com.jshvarts.todoapp.addnote

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jshvarts.todoapp.arch.EffectProducer
import com.jshvarts.todoapp.arch.MviViewModel
import com.jshvarts.todoapp.data.NoteRepository
import com.jshvarts.todoapp.data.NoteValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SAVED_STATE_HANDLE_KEY = "AddNoteViewModel_state_Key"

@HiltViewModel
class AddNoteViewModel @Inject constructor(
  private val savedStateHandle: SavedStateHandle,
  private val noteRepository: NoteRepository,
  private val noteValidator: NoteValidator
) : MviViewModel<AddNoteAction, AddNodeState>(),
  EffectProducer<AddNoteEffect> {

  override val initialState: AddNodeState = AddNodeState(
    title = "",
    saveEnabled = false
  )
  override val savedStateHandleKey: String = SAVED_STATE_HANDLE_KEY
  override val state: StateFlow<AddNodeState> = savedStateHandle.getStateFlow(savedStateHandleKey, initialState)

  private val _effect = Channel<AddNoteEffect>()
  override val effect: Flow<AddNoteEffect> = _effect.receiveAsFlow()

  override fun dispatchAction(action: AddNoteAction) {
    when (action) {
      is AddNoteAction.WriteNote -> onWriteNote(action)
      is AddNoteAction.SaveNote -> onSaveNoteRequested(action)
    }
  }

  private fun onWriteNote(action: AddNoteAction.WriteNote) {
    // todo debounce
    val saveEnabled = noteValidator.isTitleValid(action.title)
    savedStateHandle[SAVED_STATE_HANDLE_KEY] = AddNodeState(
      title = action.title, saveEnabled = saveEnabled
    )
  }

  private fun onSaveNoteRequested(action: AddNoteAction.SaveNote) {
    viewModelScope.launch {
      noteRepository.addNote(action.title)
        .onSuccess {
          _effect.send(AddNoteEffect.SaveNoteSuccess)
        }
        .onFailure {
          _effect.send(AddNoteEffect.SaveNoteFailure)
        }
    }
  }
}