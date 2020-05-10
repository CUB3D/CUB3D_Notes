package pw.cub3d.cub3_notes.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pw.cub3d.cub3_notes.core.database.dao.NotesDao
import pw.cub3d.cub3_notes.core.utils.filter
import pw.cub3d.cub3_notes.ui.nav.NewNoteNavigationController
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    val newNoteNavigationController: NewNoteNavigationController,
    private val notesDao: NotesDao
): ViewModel() {
    val searchQuery = MutableLiveData<String>()

    fun getSearchResults(query: String) = notesDao.getNoteSearchResults().filter {
        it.note.title.contains(query) ||
        it.note.text.contains(query) ||
        it.checkboxes.map { it.content }.contains(query) ||
        it.labels.map { it.title }.contains(query)
    }
}