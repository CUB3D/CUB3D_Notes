package pw.cub3d.cub3_notes.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pw.cub3d.cub3_notes.database.dao.NotesDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesViewModelFactory @Inject constructor(
    private val notesDao: NotesDao
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) = HomeViewModel(notesDao) as T

}
