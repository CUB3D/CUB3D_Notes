package pw.cub3d.cub3_notes.ui.archived

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pw.cub3d.cub3_notes.database.dao.NotesDao
import pw.cub3d.cub3_notes.database.entity.NoteAndCheckboxes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArchiveViewModelFactory @Inject constructor(
    private val notesDao: NotesDao
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) = ArchiveViewModel(notesDao) as T
}