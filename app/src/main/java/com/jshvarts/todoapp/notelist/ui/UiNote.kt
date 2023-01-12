package com.jshvarts.todoapp.notelist.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UiNote(
  val text: String
) : Parcelable
