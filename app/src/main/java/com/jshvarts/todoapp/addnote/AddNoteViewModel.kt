package com.jshvarts.todoapp.addnote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jshvarts.todoapp.addnote.ui.AddNoteUiAction
import com.jshvarts.todoapp.addnote.ui.AddNoteUiEffect
import com.jshvarts.todoapp.arch.ActionConsumer
import com.jshvarts.todoapp.arch.EffectProducer
import com.jshvarts.todoapp.data.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel @Inject constructor(
  private val noteRepository: NoteRepository
) : ViewModel(),
  ActionConsumer<AddNoteUiAction>,
  EffectProducer<AddNoteUiEffect> {

  private val _uiEffect = Channel<AddNoteUiEffect>()
  override val uiEffect: Flow<AddNoteUiEffect> = _uiEffect.receiveAsFlow()

  override fun dispatchAction(action: AddNoteUiAction) {
    when (action) {
      is AddNoteUiAction.SaveNote -> onSaveNoteRequested(action)
    }
  }

  private fun onSaveNoteRequested(action: AddNoteUiAction.SaveNote) {
    viewModelScope.launch {
      noteRepository.addNote(action.title)
        .onSuccess {
          _uiEffect.send(AddNoteUiEffect.SaveNoteSuccess)
        }
        .onFailure {
          _uiEffect.send(AddNoteUiEffect.SaveNoteFailure)
        }
    }
  }
}