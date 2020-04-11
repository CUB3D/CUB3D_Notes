package pw.cub3d.cub3_notes.ui.deleted

import androidx.lifecycle.ViewModel
import pw.cub3d.cub3_notes.database.dao.NotesDao

class DeletedNotesViewModel(notesDao: NotesDao): ViewModel() {
    val deletedNotes = notesDao.getDeletedNotes()
}
