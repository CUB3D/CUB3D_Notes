package pw.cub3d.cub3_notes.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pw.cub3d.cub3_notes.core.database.dao.NotesDao
import javax.inject.Inject

class SearchViewModel @Inject constructor(
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

inline fun <T> filterImpl(data: LiveData<List<T>>, crossinline filter: (T)->Boolean) = MediatorLiveData<List<T>>().apply {
    addSource(data) {
        val filtered = it.filter { filter(it) }
        value = filtered
    }
}

inline fun <T> LiveData<List<T>>.filter(crossinline filter: (T)->Boolean) = filterImpl(this, filter)