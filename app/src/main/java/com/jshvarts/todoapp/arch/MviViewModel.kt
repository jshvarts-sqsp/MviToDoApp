package com.jshvarts.todoapp.arch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

/**
 * If [UiEffect] is not applicable to a particular [ViewModel], pass [Nothing] when extending:
 * e.g.
 * ```
 * MviViewModel<NoteListUiAction, NoteListUiState, Nothing>(savedStateHandle)
 * ```
 */
abstract class MviViewModel<A : UiAction, S : UiState, E : UiEffect>(
  private val savedStateHandle: SavedStateHandle? = null
) : ViewModel(), ActionDispatcher<A> {
  abstract val initialState: S
  abstract val savedStateHandleKey: String?
  abstract val actionHandler: (A) -> Unit

  private val isStateInBundle: Boolean
    get() = if (savedStateHandleKey != null && savedStateHandle != null) {
      initialState != savedStateHandle[savedStateHandleKey!!]
    } else false

  override fun dispatchAction(action: A) {
    return if (isStateInBundle) Unit else actionHandler.invoke(action)
  }
}