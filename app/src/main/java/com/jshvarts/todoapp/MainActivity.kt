package com.jshvarts.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.jshvarts.todoapp.notedetail.NoteDetailViewModel
import com.jshvarts.todoapp.notedetail.ui.NoteDetailUiAction
import com.jshvarts.todoapp.notedetail.ui.NoteDetailUiState
import com.jshvarts.todoapp.notelist.NoteListViewModel
import com.jshvarts.todoapp.notelist.ui.NoteListUiAction
import com.jshvarts.todoapp.notelist.ui.NoteListUiState
import com.jshvarts.todoapp.ui.navigation.NotesAppNavHost
import com.jshvarts.todoapp.ui.theme.ToDoAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      ToDoAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
          val navController = rememberNavController()
          NotesAppNavHost(navController)
        }
      }
    }
  }
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NoteListScreen(
  onNoteClick: (Int) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: NoteListViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.dispatchAction(NoteListUiAction.LoadList)
  }

  when (uiState) {
    NoteListUiState.Loading -> LoadingState()
    is NoteListUiState.Success -> NoteListSuccessState(
      uiState as NoteListUiState.Success,
      onNoteClick = onNoteClick
    )
    is NoteListUiState.Error -> ErrorState((uiState as NoteListUiState.Error).throwable?.message.orEmpty())
  }
}

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .fillMaxSize()
  ) {
    CircularProgressIndicator()
  }
}

@Composable
fun ErrorState(
  errorMessage: String = "Generic error occurred.",
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
  ) {
    Text(text = errorMessage)
  }
}

@Composable
fun NoteListSuccessState(
  uiState: NoteListUiState.Success,
  onNoteClick: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn {
    items(uiState.data) { item ->
      Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
          .fillMaxWidth()
          .clickable {
            onNoteClick.invoke(item.id)
          }
      ) {
        Text(
          text = item.title,
          style = MaterialTheme.typography.h6,
          modifier = modifier
            .padding(16.dp)
        )
      }
    }
  }
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NoteDetailScreen(
  noteId: Int,
  modifier: Modifier = Modifier,
  viewModel: NoteDetailViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.dispatchAction(NoteDetailUiAction.LoadNote(noteId))
  }

  when (uiState) {
    NoteDetailUiState.Loading -> LoadingState()
    is NoteDetailUiState.Success -> NoteDetailSuccessState(
      uiState as NoteDetailUiState.Success,
    )
    is NoteDetailUiState.Error -> ErrorState((uiState as NoteDetailUiState.Error).throwable?.message.orEmpty())
  }
}

@Composable
fun NoteDetailSuccessState(
  uiState: NoteDetailUiState.Success,
  modifier: Modifier = Modifier
) {
  Column(
    verticalArrangement = Arrangement.spacedBy(16.dp),
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    Text(
      text = "ID: ${uiState.note.id}",
      style = MaterialTheme.typography.h3,
    )
    Text(
      text = "Text: ${uiState.note.title}",
      style = MaterialTheme.typography.h3,
    )
  }
}

