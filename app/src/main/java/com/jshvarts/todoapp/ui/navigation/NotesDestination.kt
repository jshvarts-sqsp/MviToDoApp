package com.jshvarts.todoapp.ui.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

interface NotesDestination {
  val route: String
}

object NoteList : NotesDestination {
  override val route: String = "notes"
}

object NoteDetail : NotesDestination {
  override val route: String = "note"
  const val noteIdArg = "note_id"
  val routeWithArgs = "$route/{$noteIdArg}"
  val arguments = listOf(
    navArgument(noteIdArg) {
      type = NavType.IntType
      nullable = false
    }
  )
}