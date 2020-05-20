package pw.cub3d.cub3_notes.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import pw.cub3d.cub3_notes.core.database.dao.LabelDao
import pw.cub3d.cub3_notes.core.database.dao.NotesDao
import pw.cub3d.cub3_notes.core.utils.filter
import pw.cub3d.cub3_notes.ui.nav.NewNoteNavigationController

class SearchViewModel @Inject constructor(
    val newNoteNavigationController: NewNoteNavigationController,
    private val notesDao: NotesDao,
    private val labelsDao: LabelDao
) : ViewModel() {
    val searchQuery = MutableLiveData<String>()

    fun getSearchResults() = flow {
        searchQuery.asFlow().collect { query ->
            notesDao.getAllNotes().filter {
                it.note.title.contains(query) ||
                        it.note.text.contains(query) ||
                        it.checkboxes.map { it.content }.contains(query) ||
                        it.labels.map { it.title }.contains(query)
            }.let {
                emit(it)
            }
        }
    }.asLiveData()

    fun getSearchResults(query: String) = notesDao.getNoteSearchResults().filter {
        it.note.title.contains(query) ||
        it.note.text.contains(query) ||
        it.checkboxes.map { it.content }.contains(query) ||
        it.labels.map { it.title }.contains(query)
    }

    val labels by lazy {
        labelsDao.getAll()
    }
}
