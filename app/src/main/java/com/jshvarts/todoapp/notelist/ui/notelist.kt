package com.jshvarts.todoapp.notelist.ui

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.jshvarts.todoapp.ErrorState
import com.jshvarts.todoapp.LoadingState
import com.jshvarts.todoapp.R
import com.jshvarts.todoapp.data.Note
import com.jshvarts.todoapp.notelist.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NoteListScreen(
  onNoteClick: (Int) -> Unit,
  onAddNoteClick: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: NoteListViewModel = hiltViewModel(),
  scaffoldState: ScaffoldState = rememberScaffoldState()
) {
  val uiState by viewModel.state.collectAsStateWithLifecycle()
  val refreshFailedMessage = stringResource(id = R.string.refresh_failed_message)
  val deleteFailedMessage = stringResource(id = R.string.delete_failed_message)

  val lifecycleOwner = LocalLifecycleOwner.current

  LaunchedEffect(scaffoldState.snackbarHostState) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      viewModel.effect.collect {
        when (it) {
          NoteListEffect.RefreshFailed -> {
            scaffoldState.snackbarHostState.showSnackbar(message = refreshFailedMessage)
          }
          NoteListEffect.DeleteFailed -> {
            scaffoldState.snackbarHostState.showSnackbar(message = deleteFailedMessage)
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
          Text(text = stringResource(id = R.string.notes))
        },
        backgroundColor = Color.White,
        contentColor = Color.Black
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        backgroundColor = Color.Black,
        onClick = {
          onAddNoteClick.invoke()
        }
      ) {
        Icon(
          imageVector = Icons.Outlined.Add,
          tint = Color.White,
          contentDescription = stringResource(id = R.string.add_note)
        )
      }
    },
    modifier = modifier
  ) {
    NoteListSuccessState(
      uiState = uiState,
      onNoteClick = onNoteClick,
      onSwipeToDelete = { note ->
        viewModel.dispatchAction(
          NoteListAction.SwipeToDelete(note.id)
        )
      }
    )
  }
}

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun NoteListSuccessState(
  uiState: NoteListState,
  onNoteClick: (Int) -> Unit,
  onSwipeToDelete: (Note) -> Unit,
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

  val refreshScope = rememberCoroutineScope()
  var refreshing by remember { mutableStateOf(false) }

  fun refresh() = refreshScope.launch {
    refreshing = true
    delay(300)
    viewModel.dispatchAction(NoteListAction.PullToRefresh)
    refreshing = false
  }

  val pullRefreshState = rememberPullRefreshState(refreshing, ::refresh)

  Column(modifier = modifier) {
    TabRow(
      backgroundColor = Color.White,
      contentColor = Color.Black,
      selectedTabIndex = tabIndex,
      indicator = { tabPositions ->
        TabRowDefaults.Indicator(
          Modifier.pagerTabIndicatorOffset(
            pagerState,
            tabPositions,
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
      userScrollEnabled = false
    ) { tabIndex ->
      Box(Modifier.pullRefresh(pullRefreshState)) {
        if (tabIndex == 0) {
          when (uiState.pendingTodosState) {
            is NoteListTodosState.Error -> ErrorState(uiState.pendingTodosState.throwable?.message.orEmpty())
            NoteListTodosState.Loading -> LoadingState()
            is NoteListTodosState.Success -> {
              TodosSuccessState(
                data = uiState.pendingTodosState.data,
                emptyTodosResId = emptyTodosResId,
                onNoteClick = onNoteClick,
                onSwipeToDelete = onSwipeToDelete
              )
            }
          }
        } else {
          when (uiState.completedTodosState) {
            is NoteListTodosState.Error -> ErrorState(uiState.completedTodosState.throwable?.message.orEmpty())
            NoteListTodosState.Loading -> LoadingState()
            is NoteListTodosState.Success -> {
              TodosSuccessState(
                data = uiState.completedTodosState.data,
                emptyTodosResId = emptyTodosResId,
                onNoteClick = onNoteClick,
                onSwipeToDelete = onSwipeToDelete
              )
            }
          }
        }
        PullRefreshIndicator(refreshing, pullRefreshState, Modifier.align(Alignment.TopCenter))
      }
    }
  }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TodosSuccessState(
  data: List<Note>,
  @StringRes emptyTodosResId: Int,
  onNoteClick: (Int) -> Unit,
  onSwipeToDelete: (Note) -> Unit,
  modifier: Modifier = Modifier,
) {
  if (data.isEmpty()) {
    TodosEmptyState(resId = emptyTodosResId)
  } else {
    val lazyListState: LazyListState = rememberLazyListState()
    LazyColumn(
      state = lazyListState,
      modifier = modifier
    ) {
      items(
        items = data,
        key = { it.id }
      ) { item ->
        val dismissState = rememberDismissState()
        val isDismissed = dismissState.isDismissed(DismissDirection.EndToStart)
        if (isDismissed) {
          onSwipeToDelete(item)
        }

        val degrees by animateFloatAsState(
          if (dismissState.targetValue == DismissValue.Default) 0f else -45f
        )

        SwipeToDismiss(
          state = dismissState,
          directions = setOf(DismissDirection.EndToStart),
          dismissThresholds = { FractionalThreshold(0.2f) },
          background = { SwipeToDismissBackground(degrees = degrees) },
          dismissContent = {
            NoteListItem(
              item = item,
              onNoteClick = onNoteClick
            )
          }
        )
      }
    }
  }
}

@Composable
fun SwipeToDismissBackground(degrees: Float) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.White)
      .padding(horizontal = 16.dp),
    contentAlignment = Alignment.CenterEnd
  ) {
    Icon(
      modifier = Modifier
        .rotate(degrees),
      imageVector = Icons.Filled.Delete,
      contentDescription = stringResource(id = R.string.delete_icon),
      tint = Color.White
    )
  }
}

@Composable
fun NoteListItem(
  item: Note,
  onNoteClick: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
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