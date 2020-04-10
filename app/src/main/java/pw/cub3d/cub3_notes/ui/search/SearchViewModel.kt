package pw.cub3d.cub3_notes.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pw.cub3d.cub3_notes.database.dao.NotesDao
import pw.cub3d.cub3_notes.ui.newnote.map

class SearchViewModel(private val notesDao: NotesDao): ViewModel() {
    val searchQuery = MutableLiveData<String>()

    fun getSearchResults(query: String) = notesDao.getNoteSearchResults("%$query%")
}