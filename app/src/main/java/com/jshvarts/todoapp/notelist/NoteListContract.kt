package com.jshvarts.todoapp.notelist

import android.os.Parcelable
import com.jshvarts.todoapp.arch.Action
import com.jshvarts.todoapp.arch.Effect
import com.jshvarts.todoapp.arch.State
import com.jshvarts.todoapp.data.Note
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

sealed interface NoteListAction : Action {
  object PullToRefresh : NoteListAction
  data class SwipeToDelete(
    val id: Int
  ) : NoteListAction
}

@Parcelize
sealed class NoteListTodosState : State, Parcelable {
  object Loading : NoteListTodosState()

  data class Success(
    val data: @RawValue List<Note>
  ) : NoteListTodosState()

  data class Error(
    val throwable: Throwable? = null
  ) : NoteListTodosState()
}

@Parcelize
data class NoteListState(
  val pendingTodosState: NoteListTodosState,
  val completedTodosState: NoteListTodosState
) : State, Parcelable

sealed interface NoteListEffect : Effect {
  object RefreshFailed : NoteListEffect
  object DeleteFailed : NoteListEffect
}
