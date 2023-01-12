package com.jshvarts.todoapp.arch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*

/**
 * If [UiEffect] is not applicable to a particular [ViewModel], pass [Nothing]:
 * e.g.
 * ```
 * class MyViewModel @Inject constructor(
 *   private val savedStateHandle: SavedStateHandle,
 * ) : MviViewModel<NoteListUiAction, NoteListUiState, Nothing>(savedStateHandle) {
 * ```
 */
abstract class MviNoBundleViewModel<A : UiAction, S : UiState, E : UiEffect>(
  private val savedStateHandle: SavedStateHandle
) : ViewModel() {
  abstract val initialState: S

  protected val mutableUiState = MutableStateFlow<S>(initialState)
  val uiState: StateFlow<S> = mutableUiState.asStateFlow()

  protected val mutableUiEffect = MutableSharedFlow<E>()
  val uiEffect: SharedFlow<E> = mutableUiEffect.asSharedFlow()

  abstract fun dispatchAction(action: A)
}