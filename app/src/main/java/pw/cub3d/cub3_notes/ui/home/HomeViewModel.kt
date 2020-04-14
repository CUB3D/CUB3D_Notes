package pw.cub3d.cub3_notes.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pw.cub3d.cub3_notes.database.dao.LabelDao
import pw.cub3d.cub3_notes.database.dao.NotesDao
import pw.cub3d.cub3_notes.database.entity.NoteAndCheckboxes
import pw.cub3d.cub3_notes.database.repository.NoteRepository

class HomeViewModel(
    private val notesRepository: NoteRepository,
    private val labelDao: LabelDao
) : ViewModel() {

    fun archiveNote(note: NoteAndCheckboxes) {
        GlobalScope.launch { notesRepository.archiveNote(note.note.id, true)}
    }

    val unpinnedNotes = notesRepository.getAllUnpinnedNotes()
    val pinnedNotes = notesRepository.getAllPinnedNotes()
    val labels = labelDao.getAll()
}