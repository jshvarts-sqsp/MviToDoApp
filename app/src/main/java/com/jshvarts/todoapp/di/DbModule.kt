package com.jshvarts.todoapp.di

import android.content.Context
import androidx.room.Room
import com.jshvarts.todoapp.data.local.NotesDao
import com.jshvarts.todoapp.data.local.NotesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DbModule {
  @Provides
  fun providesDatabase(
    @ApplicationContext context: Context,
  ): NotesDatabase = Room.databaseBuilder(
    context,
    NotesDatabase::class.java,
    "notes-database"
  ).fallbackToDestructiveMigration()
    .build()

  @Provides
  fun providesAuthorDao(
    database: NotesDatabase,
  ): NotesDao = database.notesDao()
}