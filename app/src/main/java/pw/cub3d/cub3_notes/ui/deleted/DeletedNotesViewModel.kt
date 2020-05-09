package pw.cub3d.cub3_notes.ui.deleted

import androidx.lifecycle.ViewModel
import pw.cub3d.cub3_notes.core.database.dao.NotesDao
import javax.inject.Inject

class DeletedNotesViewModel @Inject constructor(notesDao: NotesDao): ViewModel() {
    val deletedNotes = notesDao.getDeletedNotes()
}
