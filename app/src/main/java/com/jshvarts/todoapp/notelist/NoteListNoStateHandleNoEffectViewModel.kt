package com.jshvarts.todoapp.notelist

import androidx.lifecycle.viewModelScope
import com.jshvarts.todoapp.arch.MviViewModel
import com.jshvarts.todoapp.arch.UiResult
import com.jshvarts.todoapp.arch.asUiResult
import com.jshvarts.todoapp.data.NoteRepository
import com.jshvarts.todoapp.notelist.ui.NoteListUiAction
import com.jshvarts.todoapp.notelist.ui.NoteListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteListNoStateHandleNoEffectViewModel @Inject constructor(
  private val noteRepository: NoteRepository
) : MviViewModel<NoteListUiAction, NoteListUiState>() {

  override val initialState: NoteListUiState
    get() = NoteListUiState.Loading

  override val savedStateHandleKey = null

  override fun handleAction(action: NoteListUiAction) {
    when (action) {
      NoteListUiAction.LoadList -> onLoadList()
    }
  }

  private val _uiState = MutableStateFlow(initialState)
  override val uiState: StateFlow<NoteListUiState> = _uiState.asStateFlow()

  private fun onLoadList() {
    viewModelScope.launch {
      noteRepository
        .getNotes().asUiResult()
        .collect { result ->
          _uiState.update {
            when (result) {
              is UiResult.Loading -> NoteListUiState.Loading
              is UiResult.Success -> NoteListUiState.Success(result.data)
              is UiResult.Error -> NoteListUiState.Error(result.exception)
            }
          }
        }
    }
  }
}
