package com.jshvarts.todoapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.jshvarts.todoapp.data.Note
import com.jshvarts.todoapp.notedetail.NoteDetailViewModel
import com.jshvarts.todoapp.notedetail.ui.NoteDetailUiAction
import com.jshvarts.todoapp.notedetail.ui.NoteDetailUiState
import com.jshvarts.todoapp.notelist.NoteListViewModel
import com.jshvarts.todoapp.notelist.ui.NoteListCompletedTodosUiState
import com.jshvarts.todoapp.notelist.ui.NoteListPendingTodosUiState
import com.jshvarts.todoapp.notelist.ui.NoteListUiState
import com.jshvarts.todoapp.ui.navigation.NotesAppNavHost
import com.jshvarts.todoapp.ui.theme.ToDoAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NoteListScreen(
  onNoteClick: (Int) -> Unit,
  modifier: Modifier = Modifier,
  viewModel: NoteListViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val scaffoldState = rememberScaffoldState()

  Scaffold(
    scaffoldState = scaffoldState,
    topBar = {
      TopAppBar(
        title = {
          Text(text = stringResource(id = R.string.notes))
        },
        backgroundColor = Color.White,
        contentColor = Color.Black
      )
    }) {
    NoteListSuccessState(
      uiState = uiState,
      onNoteClick = onNoteClick
    )
  }
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

@OptIn(ExperimentalPagerApi::class)
@Composable
fun NoteListSuccessState(
  uiState: NoteListUiState,
  onNoteClick: (Int) -> Unit,
  viewModel: NoteListViewModel = hiltViewModel(),
  modifier: Modifier = Modifier
) {
  var tabIndex by remember { mutableStateOf(0) }
  val tabTitles = listOf(
    R.string.pending_todos,
    R.string.completed_todos
  )
  val pagerState = rememberPagerState()
  val scope = rememberCoroutineScope()

  Column {
    TabRow(
      backgroundColor = Color.White,
      contentColor = Color.Black,
      selectedTabIndex = tabIndex,
      indicator = { tabPositions ->
        TabRowDefaults.Indicator(
          Modifier.pagerTabIndicatorOffset(
            pagerState,
            tabPositions
          )
        )
      }) {
      tabTitles.forEachIndexed { index, titleResId ->
        Tab(selected = tabIndex == index,
          onClick = {
            tabIndex = index
            scope.launch {
              pagerState.animateScrollToPage(index)
            }
          },
          text = { Text(stringResource(id = titleResId)) })
      }
    }

    val emptyTodosResId = if (pagerState.currentPage == 0) R.string.empty_pending_todos else R.string.empty_completed_todos

    HorizontalPager(
      count = tabTitles.size,
      state = pagerState,
    ) { tabIndex ->
      if (tabIndex == 0) {
        when (uiState.pendingTodosUiState) {
          is NoteListPendingTodosUiState.Error -> ErrorState(uiState.pendingTodosUiState.throwable?.message.orEmpty())
          NoteListPendingTodosUiState.Loading -> LoadingState()
          is NoteListPendingTodosUiState.Success -> {
            TodosSuccessState(
              data = uiState.pendingTodosUiState.data,
              emptyTodosResId = emptyTodosResId,
              onNoteClick = onNoteClick
            )
          }
        }
      } else {
        when (uiState.completedTodosUiState) {
          is NoteListCompletedTodosUiState.Error -> ErrorState(uiState.completedTodosUiState.throwable?.message.orEmpty())
          NoteListCompletedTodosUiState.Loading -> LoadingState()
          is NoteListCompletedTodosUiState.Success -> {
            TodosSuccessState(
              data = uiState.completedTodosUiState.data,
              emptyTodosResId = emptyTodosResId,
              onNoteClick = onNoteClick
            )
          }
        }
      }
    }
  }
}

@Composable
fun TodosSuccessState(
  data: List<Note>,
  @StringRes emptyTodosResId: Int,
  onNoteClick: (Int) -> Unit,
  modifier: Modifier = Modifier,
) {
  if (data.isEmpty()) {
    TodosEmptyState(resId = emptyTodosResId)
  } else {
    LazyColumn {
      items(data) { item ->
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
}

@Composable
fun TodosEmptyState(@StringRes resId: Int) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .wrapContentSize(Alignment.Center)
  ) {
    Text(
      text = stringResource(id = resId),
      style = MaterialTheme.typography.h6,
      textAlign = TextAlign.Center,
      modifier = Modifier.fillMaxWidth()
    )
  }
}

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

