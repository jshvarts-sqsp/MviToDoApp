package com.jshvarts.todoapp.notelist.di

import com.jshvarts.todoapp.notelist.data.InMemoryNoteRepository
import com.jshvarts.todoapp.notelist.data.NoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
  @Binds
  abstract fun bindsNoteRepository(inMemoryNoteRepository: InMemoryNoteRepository): NoteRepository
}