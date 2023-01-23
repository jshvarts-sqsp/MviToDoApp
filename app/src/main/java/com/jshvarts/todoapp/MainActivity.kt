package com.jshvarts.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.jshvarts.todoapp.data.NoteValidator
import com.jshvarts.todoapp.ui.navigation.NotesAppNavHost
import com.jshvarts.todoapp.ui.theme.ToDoAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  @Inject
  lateinit var noteValidator: NoteValidator

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      ToDoAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
          val navController = rememberNavController()
          NotesAppNavHost(navController, noteValidator)
        }
      }
    }
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
