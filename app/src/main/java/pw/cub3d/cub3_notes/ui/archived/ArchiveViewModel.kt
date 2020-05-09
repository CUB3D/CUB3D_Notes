package pw.cub3d.cub3_notes.ui.archived

import androidx.lifecycle.ViewModel
import pw.cub3d.cub3_notes.core.database.dao.NotesDao
import javax.inject.Inject

class ArchiveViewModel @Inject constructor(
    notesDao: NotesDao
): ViewModel() {
    val archivedNotes by lazy {
        notesDao.getAllArchivedNotes()
    }
}
