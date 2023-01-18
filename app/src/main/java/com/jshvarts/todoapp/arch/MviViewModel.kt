package com.jshvarts.todoapp.arch

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.jshvarts.todoapp.notelist.ui.NoteListUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * If [UiEffect] is not applicable to a particular [ViewModel], pass [Nothing] when extending:
 * e.g.
 * ```
 * MviViewModel<NoteListUiAction, NoteListUiState, Nothing>(savedStateHandle)
 * ```
 */
abstract class MviViewModel<A : UiAction, S : UiState, E : UiEffect>(
    private val savedStateHandle: SavedStateHandle? = null
) : ViewModel() {
    protected abstract val initialState: S

    private val _uiEffect = MutableSharedFlow<E>()
    val uiEffect: SharedFlow<E> = _uiEffect.asSharedFlow()

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

    protected suspend fun dispatchEffect(effect: E) {
        _uiEffect.emit(effect)
    }
}
