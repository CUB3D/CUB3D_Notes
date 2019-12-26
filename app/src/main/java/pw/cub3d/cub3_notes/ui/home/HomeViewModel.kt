package pw.cub3d.cub3_notes.ui.home

import androidx.lifecycle.ViewModel
import pw.cub3d.cub3_notes.database.dao.NotesDao
import pw.cub3d.cub3_notes.database.repository.NoteRepository

class HomeViewModel(
    private val notesRepository: NoteRepository
) : ViewModel() {
    val unpinnedNotes = notesRepository.getAllUnpinnedNotes()
    val pinnedNotes = notesRepository.getAllPinnedNotes()
}