package com.jshvarts.todoapp.notedetail.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.jshvarts.todoapp.ErrorState
import com.jshvarts.todoapp.LoadingState
import com.jshvarts.todoapp.R
import com.jshvarts.todoapp.data.NoteValidator
import com.jshvarts.todoapp.notedetail.NoteDetailViewModel

@Composable
fun NoteDetailScreen(
  noteId: Int,
  noteValidator: NoteValidator,
  onNoteDeleted: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: NoteDetailViewModel = hiltViewModel(),
  scaffoldState: ScaffoldState = rememberScaffoldState()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  val saveFailureMessage = stringResource(id = R.string.save_failure_message)
  val saveSuccessMessage = stringResource(id = R.string.save_success_message)
  val deleteFailureMessage = stringResource(id = R.string.delete_failure_message)

  val lifecycleOwner = LocalLifecycleOwner.current

  LaunchedEffect(scaffoldState.snackbarHostState) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      viewModel.uiEffect.collect {
        when (it) {
          NoteDetailUiEffect.EditSuccess -> {
            scaffoldState.snackbarHostState.showSnackbar(message = saveSuccessMessage)
          }
          NoteDetailUiEffect.EditFailure -> {
            scaffoldState.snackbarHostState.showSnackbar(message = saveFailureMessage)
          }
          NoteDetailUiEffect.DeleteSuccess -> onNoteDeleted.invoke()
          NoteDetailUiEffect.DeleteFailure -> {
            scaffoldState.snackbarHostState.showSnackbar(message = deleteFailureMessage)
          }
        }
      }
    }
  }

  LaunchedEffect(Unit) {
    viewModel.dispatchAction(NoteDetailUiAction.LoadNote(noteId))
  }

  when (uiState) {
    NoteDetailUiState.Loading -> LoadingState()
    is NoteDetailUiState.Success -> NoteDetailSuccessState(
      uiState as NoteDetailUiState.Success,
      noteValidator = noteValidator
    )
    is NoteDetailUiState.Error -> ErrorState((uiState as NoteDetailUiState.Error).throwable?.message.orEmpty())
  }
}

@Composable
fun NoteDetailSuccessState(
  uiState: NoteDetailUiState.Success,
  noteValidator: NoteValidator,
  viewModel: NoteDetailViewModel = hiltViewModel(),
  modifier: Modifier = Modifier,
  scaffoldState: ScaffoldState = rememberScaffoldState()
) {

  if (uiState.forEditing) {
    var title by remember { mutableStateOf(uiState.note.title) }
    val saveEnabled by remember {
      derivedStateOf { noteValidator.isTitleValid(title) }
    }
    var completed by remember { mutableStateOf(false) }

    Scaffold(
      scaffoldState = scaffoldState, topBar = {
        TopAppBar(
          actions = {
            IconButton(
              onClick = {
                viewModel.dispatchAction(
                  NoteDetailUiAction.SaveNote(
                    id = uiState.note.id,
                    title = title,
                    completed = completed
                  )
                )
              }, enabled = saveEnabled
            ) {
              Icon(Icons.Filled.Done, stringResource(id = R.string.done))
            }
          },
          title = {
            Text(text = stringResource(id = R.string.editing))
          },
          backgroundColor = Color.White,
          contentColor = Color.Black,
        )
      }, modifier = modifier
    ) { paddingValues ->
      Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(16.dp)
      ) {
        TextField(
          value = title,
          onValueChange = { title = it },
          label = { Text(stringResource(id = R.string.note_title)) },
          modifier = modifier
            .padding(paddingValues)
            .fillMaxWidth(),
          shape = RoundedCornerShape(8.dp),
          colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent
          )
        )
        Spacer(modifier = modifier.padding(vertical = 16.dp))
        Text(text = "Completed? $completed")
        Switch(checked = completed, onCheckedChange = { completed = it })
        Button(onClick = {
          viewModel.dispatchAction(NoteDetailUiAction.DeleteNote(uiState.note.id))
        }) {
          Text(text = "Delete", color = Color.White)
        }
      }
    }
  } else {
    Scaffold(
      scaffoldState = scaffoldState, topBar = {
        TopAppBar(
          actions = {
            IconButton(onClick = {
              viewModel.dispatchAction(
                NoteDetailUiAction.LoadNote(
                  id = uiState.note.id,
                  forEditing = true
                )
              )
            }) {
              Icon(Icons.Filled.Edit, stringResource(id = R.string.edit_note))
            }
          },
          title = {
            Text(text = stringResource(id = R.string.note))
          },
          backgroundColor = Color.White,
          contentColor = Color.Black,
        )
      }, modifier = modifier
    ) { paddingValues ->
      Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(16.dp)
      ) {
        Text(
          text = "${uiState.note.title}",
          style = MaterialTheme.typography.h4,
        )
        Spacer(modifier = modifier.padding(vertical = 16.dp))
        Text(
          text = "Completed? ${uiState.note.completed}",
          style = MaterialTheme.typography.h5,
        )
      }
    }
  }
}
