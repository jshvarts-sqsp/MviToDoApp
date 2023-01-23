package com.jshvarts.todoapp.notedetail.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jshvarts.todoapp.ErrorState
import com.jshvarts.todoapp.LoadingState
import com.jshvarts.todoapp.notedetail.NoteDetailViewModel

@Composable
fun NoteDetailScreen(
  noteId: Int?,
  modifier: Modifier = Modifier,
  viewModel: NoteDetailViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LaunchedEffect(Unit) {
    if (noteId != null) {
      viewModel.dispatchAction(NoteDetailUiAction.LoadNote(noteId))
    } else {
      viewModel.dispatchAction(NoteDetailUiAction.NewNote)
    }
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