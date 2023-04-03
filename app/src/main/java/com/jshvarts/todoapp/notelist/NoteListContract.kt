package com.jshvarts.todoapp.notelist

import com.jshvarts.todoapp.arch.Action
import com.jshvarts.todoapp.arch.State

sealed interface NoteListAction : Action {
  object AddTextBlock : NoteListAction
}

data class NoteListState(
  val blocks: List<TextBlock> = emptyList()
) : State

data class TextBlock(
  val id: String
)
