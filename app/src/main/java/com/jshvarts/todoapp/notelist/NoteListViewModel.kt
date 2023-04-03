package com.jshvarts.todoapp.notelist

import com.jshvarts.todoapp.arch.MviViewModel
import com.jshvarts.todoapp.data.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
  private val noteRepository: NoteRepository
) : MviViewModel<NoteListAction, NoteListState>() {

  override val savedStateHandleKey: String? = null

  override val initialState: NoteListState = NoteListState()

  private val _state = MutableStateFlow(initialState)
  override val state: StateFlow<NoteListState> = _state.asStateFlow()

  override fun dispatchAction(action: NoteListAction) {
    when (action) {
      NoteListAction.AddTextBlock -> {
        val newTextBlock = TextBlock(id = UUID.randomUUID().toString())
        _state.update {
          it.copy(blocks = buildList {
            addAll(it.blocks)
            add(newTextBlock)
          })
        }
      }
    }
  }
}
