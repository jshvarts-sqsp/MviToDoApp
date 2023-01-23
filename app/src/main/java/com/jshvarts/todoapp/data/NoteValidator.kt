package com.jshvarts.todoapp.data

import javax.inject.Inject

class NoteValidator @Inject constructor() {
  fun isTitleValid(title: String): Boolean {
    return title.isNotBlank()
  }
}