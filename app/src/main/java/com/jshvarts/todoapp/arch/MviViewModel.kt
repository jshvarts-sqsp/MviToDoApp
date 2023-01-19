package com.jshvarts.todoapp.arch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

abstract class MviViewModel<A : UiAction, S : UiState>(
  private val savedStateHandle: SavedStateHandle? = null
) : ViewModel() {
  protected abstract val initialState: S

  abstract val uiState: StateFlow<S>

  protected abstract val savedStateHandleKey: String?
  protected abstract fun handleAction(action: A)

  private val isStateInBundle: Boolean
    get() = if (savedStateHandleKey != null && savedStateHandle != null) {
      initialState != savedStateHandle[savedStateHandleKey!!]
    } else false

  fun dispatchAction(action: A) {
    return if (isStateInBundle) Unit else handleAction(action)
  }

  interface HasUiEffect<E : UiEffect> {
    val uiEffect: SharedFlow<E>
  }
}
