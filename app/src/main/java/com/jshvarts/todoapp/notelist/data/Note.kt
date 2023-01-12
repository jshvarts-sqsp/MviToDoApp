package com.jshvarts.todoapp.notelist.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
  val text: String
) : Parcelable