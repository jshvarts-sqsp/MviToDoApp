package com.jshvarts.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jshvarts.todoapp.notelist.NoteListViewModel
import com.jshvarts.todoapp.notelist.ui.NoteListUiAction
import com.jshvarts.todoapp.notelist.ui.NoteListUiState
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
          NoteListScreen()
        }
      }
    }
  }
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NoteListScreen(viewModel: NoteListViewModel = viewModel()) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    viewModel.dispatchAction(NoteListUiAction.LoadList)
  }

  when (uiState) {
    NoteListUiState.Loading -> LoadingState()
    is NoteListUiState.Success -> SuccessState(uiState as NoteListUiState.Success)
    is NoteListUiState.Error -> ErrorState(uiState as NoteListUiState.Error)
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
  errorState: NoteListUiState.Error,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
  ) {
    errorState.throwable
  }
}


@Composable
fun SuccessState(
  uiState: NoteListUiState.Success,
  modifier: Modifier = Modifier
) {
  LazyColumn {
    items(uiState.data) { item ->
      Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Text(
          text = item.text,
          style = MaterialTheme.typography.h4
        )
      }
    }
  }
}
