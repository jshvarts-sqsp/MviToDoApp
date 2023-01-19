package com.jshvarts.todoapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
  val id: String,
  val text: String
) : Parcelable