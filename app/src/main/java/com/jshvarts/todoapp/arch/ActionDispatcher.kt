package com.jshvarts.todoapp.arch

interface ActionDispatcher<A : UiAction> {
  fun dispatchAction(action: A, block: () -> Unit)
  fun dispatchAction(action: A)
}