package com.jshvarts.todoapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
  @Query(value = "SELECT * FROM note ORDER BY id DESC")
  fun getNotesAsFlow(): Flow<List<NoteEntity>>

  @Query(value = "SELECT * FROM note WHERE id = :id")
  fun getNoteAsFlow(id: Int): Flow<NoteEntity>

  @Query(value = "SELECT * FROM note WHERE id = :id")
  suspend fun getNote(id: Int): NoteEntity

  @Delete
  suspend fun deleteNote(entity: NoteEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNotes(notes: List<NoteEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertNote(notes: NoteEntity)

  @Query(value = "SELECT max(id) FROM note")
  suspend fun getCurrentMaxNoteId(): Int
}