package com.jshvarts.todoapp.arch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * If [UiEffect] is not applicable to a particular [ViewModel], pass [Nothing]:
 * e.g.
 * ```
 * class MyViewModel @Inject constructor(
 *   private val savedStateHandle: SavedStateHandle,
 * ) : MviViewModel<NoteListUiAction, NoteListUiState, Nothing>(savedStateHandle) {
 * ```
 */
abstract class MviViewModel<A : UiAction, S : UiState, E : UiEffect>(
  private val savedStateHandle: SavedStateHandle
) : ViewModel() {
  abstract val initialState: S
  protected abstract val savedStateHandleKey: String

  val uiState: StateFlow<S> = savedStateHandle.getStateFlow(savedStateHandleKey, initialState)

  protected val mutableUiEffect = MutableSharedFlow<E>()
  val uiEffect: SharedFlow<E> = mutableUiEffect.asSharedFlow()

  abstract fun dispatchAction(action: A)

  /**
   * To be called when dispatchAction(A) is entered.
   * If true, no need to produce new state--one from bundle will be used
   */
  protected fun isStateInBundle(): Boolean {
    return initialState != savedStateHandle[savedStateHandleKey]
  }
}