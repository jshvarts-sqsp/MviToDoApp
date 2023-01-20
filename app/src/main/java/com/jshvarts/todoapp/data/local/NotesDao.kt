package com.jshvarts.todoapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
  @Query(value = "SELECT * FROM note")
  fun getNotes(): Flow<List<NoteEntity>>

  @Query(value = "SELECT * FROM note WHERE id = :id")
  fun getNote(id: Int): Flow<NoteEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotes(movies: List<NoteEntity>): List<Long>
}