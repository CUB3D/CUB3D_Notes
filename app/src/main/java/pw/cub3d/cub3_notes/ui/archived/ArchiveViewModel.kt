package pw.cub3d.cub3_notes.ui.archived

import androidx.lifecycle.ViewModel
import pw.cub3d.cub3_notes.core.database.dao.NotesDao
import pw.cub3d.cub3_notes.core.manager.SettingsManager
import pw.cub3d.cub3_notes.ui.nav.NewNoteNavigationController
import javax.inject.Inject

class ArchiveViewModel @Inject constructor(
    val settingsManager: SettingsManager,
    val noteNavigationController: NewNoteNavigationController,
    notesDao: NotesDao
): ViewModel() {
    val archivedNotes by lazy {
        notesDao.getAllArchivedNotes()
    }
}
