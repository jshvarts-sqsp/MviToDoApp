package com.jshvarts.todoapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jshvarts.todoapp.NoteDetailScreen
import com.jshvarts.todoapp.NoteListScreen

@Composable
fun NotesAppNavHost(
  navController: NavHostController,
  modifier: Modifier = Modifier
) {
  NavHost(
    navController,
    startDestination = NoteList.route,
    modifier = modifier
  ) {
    composable(route = NoteList.route) {
      NoteListScreen(
        onNoteClick = { noteId ->
          navController.navigateToNote(noteId)
        }
      )
    }
    composable(
      route = NoteDetail.routeWithArgs,
      arguments = NoteDetail.arguments
    ) { navBackStackEntry ->
      val noteId = navBackStackEntry.arguments?.getString(NoteDetail.noteIdArg)!!
      NoteDetailScreen(noteId)
    }
  }
}

fun NavHostController.navigateSingleTopTo(route: String) =
  this.navigate(route) {
    // Pop up to the start destination of the graph to avoid building up a large stack of destinations
    // on the back stack as users select items
    popUpTo(
      this@navigateSingleTopTo.graph.findStartDestination().id
    ) {
      saveState = true
    }
    // Avoid multiple copies of the same destination when re-selecting the same item
    launchSingleTop = true
    // Restore state when re-selecting a previously selected item
    restoreState = true
  }

private fun NavHostController.navigateToNote(noteId: String) {
  this.navigateSingleTopTo("${NoteDetail.route}/$noteId")
}
