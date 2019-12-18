package pw.cub3d.cub3_notes.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import pw.cub3d.cub3_notes.database.NotesDao

class NotesViewModelFactory(
    private val notesDao: NotesDao
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) = HomeViewModel(notesDao) as T

}
