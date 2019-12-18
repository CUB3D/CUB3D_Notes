package pw.cub3d.cub3_notes.ui.newnote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import pw.cub3d.cub3_notes.database.NotesDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewNoteViewModelFactory @Inject constructor(
    private val notesDao: NotesDao
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) = NewNoteViewModel(notesDao) as T
}