package com.jshvarts.todoapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
  val id: Int = 0,
  val title: String,
  val completed: Boolean
) : Parcelable