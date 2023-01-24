package com.jshvarts.todoapp.arch

import kotlinx.coroutines.flow.*

interface Action

interface State

interface Effect

interface StateProducer<S : State> {
  val initialState: S
  val state: StateFlow<S>
  val savedStateHandleKey: String?
}

interface EffectProducer<E : Effect> {
  val effect: Flow<E>
}

sealed interface Result<out T> {
  data class Success<T>(val data: T) : Result<T>
  data class Error(val exception: Throwable? = null) : Result<Nothing>
  object Loading : Result<Nothing>
}

fun <T> Flow<T>.asResult(): Flow<Result<T>> {
  return this
    .map<T, Result<T>> {
      Result.Success(it)
    }
    .onStart { emit(Result.Loading) }
    .catch { emit(Result.Error(it)) }
}