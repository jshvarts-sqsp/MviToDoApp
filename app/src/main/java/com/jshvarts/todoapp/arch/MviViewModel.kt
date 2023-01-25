package com.jshvarts.todoapp.arch

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

abstract class MviViewModel<A : Action, S : State> : ViewModel() {
  abstract fun dispatchAction(action: A)

  abstract val initialState: S
  abstract val savedStateHandleKey: String?
  abstract val state: StateFlow<S>
}