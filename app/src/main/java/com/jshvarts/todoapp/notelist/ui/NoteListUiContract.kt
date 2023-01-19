package com.jshvarts.todoapp.notelist.ui

import android.os.Parcelable
import com.jshvarts.todoapp.arch.UiAction
import com.jshvarts.todoapp.arch.UiEffect
import com.jshvarts.todoapp.arch.UiState
import com.jshvarts.todoapp.data.Note
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

sealed interface NoteListUiAction : UiAction {
  object LoadList : NoteListUiAction
}

@Parcelize
sealed class NoteListUiState : UiState, Parcelable {
  object Loading : NoteListUiState()

  data class Success(
    val data: @RawValue List<Note>
  ) : NoteListUiState()

  data class Error(
    val throwable: Throwable? = null
  ) : NoteListUiState()
}

sealed interface NoteListUiEffect : UiEffect {
  object MessageDataLoading : NoteListUiEffect
}
