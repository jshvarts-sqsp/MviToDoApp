package com.jshvarts.todoapp.addnote.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
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
import com.jshvarts.todoapp.R
import com.jshvarts.todoapp.addnote.AddNoteAction
import com.jshvarts.todoapp.addnote.AddNoteEffect
import com.jshvarts.todoapp.addnote.AddNoteViewModel

@Composable
fun AddNoteScreen(
  onNoteSaved: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: AddNoteViewModel = hiltViewModel(),
  scaffoldState: ScaffoldState = rememberScaffoldState()
) {

  val state by viewModel.state.collectAsStateWithLifecycle()

  val saveFailureMessage = stringResource(id = R.string.save_failure_message)
  val saveSuccessMessage = stringResource(id = R.string.save_success_message)

  val lifecycleOwner = LocalLifecycleOwner.current

  LaunchedEffect(scaffoldState.snackbarHostState) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      viewModel.effect.collect {
        when (it) {
          AddNoteEffect.SaveNoteFailure -> {
            scaffoldState.snackbarHostState.showSnackbar(message = saveFailureMessage)
          }
          AddNoteEffect.SaveNoteSuccess -> {
            scaffoldState.snackbarHostState.showSnackbar(message = saveSuccessMessage)
            onNoteSaved.invoke()
          }
        }
      }
    }
  }

  Scaffold(
    scaffoldState = scaffoldState,
    topBar = {
      TopAppBar(
        title = {
          Text(text = stringResource(id = R.string.add_note))
        },
        backgroundColor = Color.White,
        contentColor = Color.Black,
        actions = {
          IconButton(
            onClick = {
              viewModel.dispatchAction(AddNoteAction.SaveNote(state.title))
            },
            enabled = state.saveEnabled
          ) {
            Icon(Icons.Filled.Done, stringResource(id = R.string.add_note))
          }
        },
      )
    },
    modifier = modifier
  ) { paddingValues ->
    TextField(
      value = state.title,
      onValueChange = { viewModel.dispatchAction(AddNoteAction.WriteNote(it)) },
      label = { Text(stringResource(id = R.string.note_title)) },
      modifier = modifier
        .padding(paddingValues)
        .fillMaxWidth(),
      shape = RoundedCornerShape(8.dp),
      colors = TextFieldDefaults.textFieldColors(
        backgroundColor = Color.Transparent
      )
    )
  }
}
