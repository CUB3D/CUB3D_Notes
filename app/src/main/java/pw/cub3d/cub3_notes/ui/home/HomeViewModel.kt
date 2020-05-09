package pw.cub3d.cub3_notes.ui.home

import androidx.lifecycle.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pw.cub3d.cub3_notes.core.database.dao.LabelDao
import pw.cub3d.cub3_notes.core.database.entity.NoteAndCheckboxes
import pw.cub3d.cub3_notes.core.database.repository.NoteRepository
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val notesRepository: NoteRepository,
    private val labelDao: LabelDao
) : ViewModel() {

    fun archiveNote(note: NoteAndCheckboxes) {
        GlobalScope.launch { notesRepository.archiveNote(note.note.id, true)}
    }

    fun upadateNotePosition(cb: NoteAndCheckboxes, position: Long) {
        GlobalScope.launch { notesRepository.setNotePosition(cb.note.id, position) }
    }

    fun setShowOnlyReminders(b: Boolean) {
        showOnlyReminders.value = b
    }

    private val showOnlyReminders = MutableLiveData(false)

    val unpinnedNotes = showOnlyReminders.switchMap {
        when (it) {
            true -> notesRepository.getAllUnpinnedReminders()
            false -> notesRepository.getAllUnpinnedNotes()
        }
    }
    val pinnedNotes = showOnlyReminders.switchMap {
        when (it) {
            true -> notesRepository.getAllPinnedReminders()
            false -> notesRepository.getAllPinnedNotes()
        }
    }
    val labels = labelDao.getAll()
}