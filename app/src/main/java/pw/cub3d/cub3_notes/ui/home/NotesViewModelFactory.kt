package pw.cub3d.cub3_notes.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pw.cub3d.cub3_notes.database.dao.LabelDao
import pw.cub3d.cub3_notes.database.repository.NoteRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesViewModelFactory @Inject constructor(
    private val notesRepository: NoteRepository,
    private val labelDao: LabelDao
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>) = HomeViewModel(notesRepository, labelDao) as T

}
