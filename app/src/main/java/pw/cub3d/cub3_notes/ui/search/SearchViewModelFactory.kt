package pw.cub3d.cub3_notes.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pw.cub3d.cub3_notes.database.dao.NotesDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchViewModelFactory @Inject constructor(
    val notesDao: NotesDao
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) = SearchViewModel(notesDao) as T
}