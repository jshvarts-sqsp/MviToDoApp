package com.jshvarts.todoapp.arch

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ActionConsumer<A : UiAction> {
  fun dispatchAction(action: A)
}

interface StateProducer<S : UiState> {
  val initialState: S
  val uiState: StateFlow<S>
  val savedStateHandleKey: String?
}

interface EffectProducer<E : UiEffect> {
  val uiEffect: Flow<E>
}