package com.jshvarts.todoapp.addnote

import androidx.lifecycle.viewModelScope
import com.jshvarts.todoapp.arch.EffectProducer
import com.jshvarts.todoapp.arch.MviViewModel
import com.jshvarts.todoapp.data.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel @Inject constructor(
  private val noteRepository: NoteRepository
) : MviViewModel<AddNoteAction>(),
  EffectProducer<AddNoteEffect> {

  private val _effect = Channel<AddNoteEffect>()
  override val effect: Flow<AddNoteEffect> = _effect.receiveAsFlow()

  override fun dispatchAction(action: AddNoteAction) {
    when (action) {
      is AddNoteAction.SaveNote -> onSaveNoteRequested(action)
    }
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