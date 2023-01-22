package com.jshvarts.todoapp.notelist.ui

import android.os.Parcelable
import com.jshvarts.todoapp.arch.UiAction
import com.jshvarts.todoapp.arch.UiEffect
import com.jshvarts.todoapp.arch.UiState
import com.jshvarts.todoapp.data.Note
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

sealed interface NoteListUiAction : UiAction {
  object PullToRefresh : NoteListUiAction
}

@Parcelize
sealed class NoteListTodosUiState : UiState, Parcelable {
  object Loading : NoteListTodosUiState()

  data class Success(
    val data: @RawValue List<Note>
  ) : NoteListTodosUiState()

  data class Error(
    val throwable: Throwable? = null
  ) : NoteListTodosUiState()
}

@Parcelize
data class NoteListUiState(
  val pendingTodosUiState: NoteListTodosUiState,
  val completedTodosUiState: NoteListTodosUiState
) : UiState, Parcelable

sealed interface NoteListUiEffect : UiEffect {
  object RefreshFailed : NoteListUiEffect
}
