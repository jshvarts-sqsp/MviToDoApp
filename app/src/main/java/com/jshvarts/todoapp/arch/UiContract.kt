package com.jshvarts.todoapp.arch

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

interface UiAction

interface UiState

interface UiEffect

interface UiMapper<T, R> {
  fun toUi(item: T): R
}

sealed interface UiResult<out T> {
  data class Success<T>(val data: T) : UiResult<T>
  data class Error(val exception: Throwable? = null) : UiResult<Nothing>
  object Loading : UiResult<Nothing>
}

fun <T> Flow<T>.asUiResult(): Flow<UiResult<T>> {
  return this
    .map<T, UiResult<T>> {
      UiResult.Success(it)
    }
    .onStart { emit(UiResult.Loading) }
    .catch { emit(UiResult.Error(it)) }
}