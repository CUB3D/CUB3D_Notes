package pw.cub3d.cub3_notes.ui.archived

import androidx.lifecycle.ViewModel
import pw.cub3d.cub3_notes.database.dao.NotesDao

class ArchiveViewModel(notesDao: NotesDao) : ViewModel() {
    val archivedNotes = notesDao.getAllArchivedNotes()
}
