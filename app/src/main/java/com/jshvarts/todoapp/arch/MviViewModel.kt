package com.jshvarts.todoapp.arch

import androidx.lifecycle.ViewModel

abstract class MviViewModel<A : Action> : ViewModel() {
  abstract fun dispatchAction(action: A)
}