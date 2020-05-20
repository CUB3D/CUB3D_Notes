package pw.cub3d.cub3_notes.ui.home

import androidx.lifecycle.*
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import pw.cub3d.cub3_notes.core.database.dao.LabelDao
import pw.cub3d.cub3_notes.core.database.entity.NoteAndCheckboxes
import pw.cub3d.cub3_notes.core.database.repository.FilterType
import pw.cub3d.cub3_notes.core.database.repository.NoteRepository
import pw.cub3d.cub3_notes.core.database.repository.SortTypes
import pw.cub3d.cub3_notes.core.manager.AudioManager
import pw.cub3d.cub3_notes.core.manager.SettingsManager
import pw.cub3d.cub3_notes.core.manager.StorageManager
import pw.cub3d.cub3_notes.ui.nav.NewNoteNavigationController

class HomeViewModel @Inject constructor(
    val newNoteNavigationController: NewNoteNavigationController,
    val storageManager: StorageManager,
    val settingsManager: SettingsManager,
    val audioManager: AudioManager,
    private val notesRepository: NoteRepository,
    private val labelDao: LabelDao
) : ViewModel() {

    fun archiveNote(note: NoteAndCheckboxes) {
        GlobalScope.launch { notesRepository.archiveNote(note.note.id, true) }
    }

    fun upadateNotePosition(cb: NoteAndCheckboxes, position: Long) {
        GlobalScope.launch { notesRepository.setNotePosition(cb.note.id, position) }
    }

    fun setShowOnlyReminders(b: Boolean) {
        filter.postValue(FilterType.REMINDERS)
    }

    val labels = labelDao.getAll()

    val filter = MutableLiveData(FilterType.ALL)
    val sort = MutableLiveData(SortTypes.MANUAL)
    val pinned by lazy {
        liveData<List<NoteAndCheckboxes>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            emitSource(notesRepository.getNotes(filter, sort, true, archived = false).flowOn(Dispatchers.IO).asLiveData())
        }
    }
    val unpinned by lazy {
        liveData<List<NoteAndCheckboxes>>(viewModelScope.coroutineContext + Dispatchers.IO) {
            emitSource(notesRepository.getNotes(filter, sort, false, archived = false).flowOn(Dispatchers.IO).asLiveData())
        }
    }
}
