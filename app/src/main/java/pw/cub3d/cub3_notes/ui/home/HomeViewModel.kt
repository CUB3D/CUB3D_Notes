package pw.cub3d.cub3_notes.ui.home

import androidx.lifecycle.ViewModel
import pw.cub3d.cub3_notes.database.dao.NotesDao

class HomeViewModel(
    private val notesDao: NotesDao
) : ViewModel() {
    val unpinnedNotes = notesDao.getAllUnpinnedNotes()
    val pinnedNotes = notesDao.getAllPinnedNotes()
}