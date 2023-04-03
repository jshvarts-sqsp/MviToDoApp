package com.jshvarts.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jshvarts.todoapp.notelist.NoteListAction
import com.jshvarts.todoapp.notelist.NoteListState
import com.jshvarts.todoapp.notelist.NoteListViewModel
import com.jshvarts.todoapp.notelist.TextBlock
import com.jshvarts.todoapp.ui.theme.ToDoAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  private val viewModel by viewModels<NoteListViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    WindowCompat.setDecorFitsSystemWindows(window, false)

    setContent {
      ToDoAppTheme {
        Scaffold(
          backgroundColor = Color.White,
          contentColor = Color.Black,
          topBar = {
            BlogEditorTopAppBar(
              onAddBlockClick = {
                viewModel.dispatchAction(NoteListAction.AddTextBlock)
              }
            )
          }
        ) { paddingValues ->
          NoteListScreen(
            modifier = Modifier
              .padding(paddingValues)
          )
        }
      }
    }
  }
}

@Composable
fun NoteListScreen(
  viewModel: NoteListViewModel = hiltViewModel(),
  modifier: Modifier = Modifier
) {
  val state: NoteListState by viewModel.state.collectAsStateWithLifecycle()

  LazyColumn(
    state = rememberLazyListState(),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    modifier = modifier
  ) {
    items(state.blocks) { block ->
      TextBlockItem(block = block)
    }
  }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class)
@Composable
fun TextBlockItem(
  block: TextBlock,
  modifier: Modifier = Modifier
) {
  val textState = remember { mutableStateOf(TextFieldValue()) }
  val bringIntoViewRequester = remember { BringIntoViewRequester() }
  val scope: CoroutineScope = rememberCoroutineScope()

  val focusManager = LocalFocusManager.current
  val interactionSource = remember { MutableInteractionSource() }
  val interactionSourceState = interactionSource.collectIsFocusedAsState()
  val isImeVisible = WindowInsets.isImeVisible
  val focusRequester = FocusRequester()

//  LaunchedEffect(isImeVisible, interactionSourceState.value) {
//    if (isImeVisible && interactionSourceState.value) {
//      scope.launch {
//        delay(300)
//        bringIntoViewRequester.bringIntoView()
//      }
//    }
//  }

  //val focusRequester = remember { FocusRequester() }
  //val scrollState = rememberScrollState(0)

//  LaunchedEffect(scrollState.maxValue) {
//    scrollState.scrollTo(scrollState.maxValue)
//    // or
//    // scrollState.animateScrollTo(scrollState.maxValue)
//  }

  TextField(
    placeholder = {
      Text(
        text = "Write here...",
        color = Color.Black
      )
    },
    value = textState.value,
    onValueChange = { textState.value = it },
    modifier = modifier
      .fillMaxWidth()
      //.imePadding()
      .background(Color.LightGray)
      //.verticalScroll(scrollState)
      //.focusRequester(focusRequester)
      //.onFocusChanged { color = if (it.isFocused) Green else Black }
      //.focusRequester(focusRequester)
      .bringIntoViewRequester(bringIntoViewRequester)
      .onFocusEvent {
        println("jls test ${block.id} isFocused ${it.isFocused}")
        if (it.isFocused) {
          scope.launch {
            delay(300)
            bringIntoViewRequester.bringIntoView()
          }
        }
      }
      //.bringIntoViewRequester(bringIntoViewRequester)
//      .onFocusEvent {
//        println("jls test ${block.id} isFocused ${it.isFocused}")
//        if (it.isFocused) {
//          scope.launch {
//            delay(300)
//            bringIntoViewRequester.bringIntoView()
//          }
//        }
//      }
  )
}

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .wrapContentSize(Alignment.Center)
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
fun BlogEditorTopAppBar(
  modifier: Modifier = Modifier,
  onAddBlockClick: () -> Unit
) {
  TopAppBar(
    title = {
      Text(text = "Write a post")
    },
    actions = {
      AddBlockMenu { onAddBlockClick() }
    },
    backgroundColor = Color.White,
    modifier = modifier
      .statusBarsPadding()
  )
}

@Composable
fun AddBlockMenu(
  onClick: () -> Unit
) {
  IconButton(onClick = {
    onClick()
  }) {
    Icon(
      imageVector = Icons.Default.Add,
      contentDescription = "Add text block"
    )
  }
}

@Preview
@Composable
fun EditorTopAppBarPreview() {
  Surface {
    BlogEditorTopAppBar(onAddBlockClick = {})
  }
}