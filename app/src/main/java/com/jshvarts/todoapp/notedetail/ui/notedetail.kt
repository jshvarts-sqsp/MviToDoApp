package com.jshvarts.todoapp.notedetail.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.jshvarts.todoapp.notedetail.NoteDetailAction
import com.jshvarts.todoapp.notedetail.NoteDetailEffect
import com.jshvarts.todoapp.notedetail.NoteDetailState
import com.jshvarts.todoapp.notedetail.NoteDetailViewModel

@Composable
fun NoteDetailScreen(
  noteId: Int,
  onNoteDeleted: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: NoteDetailViewModel = hiltViewModel(),
  scaffoldState: ScaffoldState = rememberScaffoldState()
) {
  val state by viewModel.state.collectAsStateWithLifecycle()

  val onTitleChanged: (String) -> Unit = {
    val id = (state as NoteDetailState.EditNote).id
    val completed = (state as NoteDetailState.EditNote).completed
    viewModel.dispatchAction(
      NoteDetailAction.EditNote(
        id = id,
        title = it,
        completed = completed,
        forEditing = true
      )
    )
  }
  val onCompletedChanged: (Boolean) -> Unit = {
    val id = (state as NoteDetailState.EditNote).id
    val title = (state as NoteDetailState.EditNote).title
    viewModel.dispatchAction(
      NoteDetailAction.EditNote(
        id = id,
        title = title,
        completed = it,
        forEditing = true
      )
    )
  }
  val onDeleteNote: () -> Unit = {
    val id = (state as NoteDetailState.EditNote).id
    viewModel.dispatchAction(
      NoteDetailAction.DeleteNote(
        id = id
      )
    )
  }
  val onSaveNote: (String, Boolean) -> Unit = { title, completed ->
    val id = (state as NoteDetailState.EditNote).id
    viewModel.dispatchAction(
      NoteDetailAction.SaveNote(
        id = id,
        title = title,
        completed = completed
      )
    )
  }
  val onEditClick: () -> Unit = {
    val note = (state as NoteDetailState.Success).note
    viewModel.dispatchAction(
      NoteDetailAction.EditNote(
        id = note.id,
        title = note.title,
        completed = note.completed,
        forEditing = true
      )
    )
  }

  val saveFailureMessage = stringResource(id = R.string.save_failure_message)
  val saveSuccessMessage = stringResource(id = R.string.save_success_message)
  val deleteFailureMessage = stringResource(id = R.string.delete_failure_message)

  val lifecycleOwner = LocalLifecycleOwner.current

  LaunchedEffect(scaffoldState.snackbarHostState) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      viewModel.effect.collect {
        when (it) {
          NoteDetailEffect.EditSuccess -> {
            scaffoldState.snackbarHostState.showSnackbar(message = saveSuccessMessage)
          }
          NoteDetailEffect.EditFailure -> {
            scaffoldState.snackbarHostState.showSnackbar(message = saveFailureMessage)
          }
          NoteDetailEffect.DeleteSuccess -> onNoteDeleted.invoke()
          NoteDetailEffect.DeleteFailure -> {
            scaffoldState.snackbarHostState.showSnackbar(message = deleteFailureMessage)
          }
        }
      }
    }
  }

  LaunchedEffect(Unit) {
    viewModel.dispatchAction(NoteDetailAction.LoadNote(noteId))
  }

  when (state) {
    NoteDetailState.Loading -> LoadingState()
    is NoteDetailState.Success -> NoteDetailSuccessState(
      state as NoteDetailState.Success,
      onEditClick = onEditClick
    )
    is NoteDetailState.Error -> ErrorState((state as NoteDetailState.Error).throwable?.message.orEmpty())
    is NoteDetailState.EditNote -> NoteDetailEditState(
      state = state as NoteDetailState.EditNote,
      onTitleChanged = onTitleChanged,
      onCompletedChanged = onCompletedChanged,
      onDeleteNote = onDeleteNote,
      onSaveNote = onSaveNote
    )
  }
}

@Composable
fun NoteDetailEditState(
  state: NoteDetailState.EditNote,
  onTitleChanged: (String) -> Unit,
  onCompletedChanged: (Boolean) -> Unit,
  onDeleteNote: () -> Unit,
  onSaveNote: (String, Boolean) -> Unit,
  modifier: Modifier = Modifier,
  scaffoldState: ScaffoldState = rememberScaffoldState()
) {
  Scaffold(
    scaffoldState = scaffoldState,
    topBar = {
      TopAppBar(
        actions = {
          IconButton(
            onClick = {
              onSaveNote(state.title, state.completed)
            }, enabled = state.saveEnabled
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
        value = state.title,
        onValueChange = { onTitleChanged(it) },
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
      Text(text = "Completed? ${state.completed}")
      Switch(checked = state.completed, onCheckedChange = { onCompletedChanged(it) })
      Button(onClick = { onDeleteNote() }) {
        Text(text = "Delete", color = Color.White)
      }
    }
  }
}

@Composable
fun NoteDetailSuccessState(
  state: NoteDetailState.Success,
  onEditClick: () -> Unit,
  modifier: Modifier = Modifier,
  scaffoldState: ScaffoldState = rememberScaffoldState()
) {
  Scaffold(
    scaffoldState = scaffoldState, topBar = {
      TopAppBar(
        actions = {
          IconButton(onClick = { onEditClick() }) {
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
        text = "${state.note.title}",
        style = MaterialTheme.typography.h4,
      )
      Spacer(modifier = modifier.padding(vertical = 16.dp))
      Text(
        text = "Completed? ${state.note.completed}",
        style = MaterialTheme.typography.h5,
      )
    }
  }
}
