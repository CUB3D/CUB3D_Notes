package pw.cub3d.cub3_notes.ui.deleted

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pw.cub3d.cub3_notes.database.dao.NotesDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeletedNotesViewModelFactory @Inject constructor(
    private val notesDao: NotesDao
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        DeletedNotesViewModel(notesDao) as T
}
