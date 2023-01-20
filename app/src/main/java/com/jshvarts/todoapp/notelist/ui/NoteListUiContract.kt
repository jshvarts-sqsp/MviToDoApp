package com.jshvarts.todoapp.notelist.ui

import android.os.Parcelable
import com.jshvarts.todoapp.arch.UiEffect
import com.jshvarts.todoapp.arch.UiState
import com.jshvarts.todoapp.data.Note
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
sealed class NoteListPendingTodosUiState : UiState, Parcelable {
  object Loading : NoteListPendingTodosUiState()

  data class Success(
    val data: @RawValue List<Note>
  ) : NoteListPendingTodosUiState()

  data class Error(
    val throwable: Throwable? = null
  ) : NoteListPendingTodosUiState()
}

@Parcelize
sealed class NoteListCompletedTodosUiState : UiState, Parcelable {
  object Loading : NoteListCompletedTodosUiState()

  data class Success(
    val data: @RawValue List<Note>
  ) : NoteListCompletedTodosUiState()

  data class Error(
    val throwable: Throwable? = null
  ) : NoteListCompletedTodosUiState()
}

@Parcelize
data class NoteListUiState(
  val pendingTodosUiState: NoteListPendingTodosUiState,
  val completedTodosUiState: NoteListCompletedTodosUiState
) : UiState, Parcelable

sealed interface NoteListUiEffect : UiEffect {
  object MessageDataLoading : NoteListUiEffect
}
