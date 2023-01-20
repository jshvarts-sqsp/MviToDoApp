package com.jshvarts.todoapp.di

import com.jshvarts.todoapp.data.NoteRepository
import com.jshvarts.todoapp.data.OfflineFirstNoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
  @Binds
  abstract fun bindsNotesRepository(offlineFirstNoteRepository: OfflineFirstNoteRepository): NoteRepository
}