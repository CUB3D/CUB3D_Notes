package pw.cub3d.cub3_notes.ui.archived

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import javax.inject.Inject
import pw.cub3d.cub3_notes.core.database.repository.FilterType
import pw.cub3d.cub3_notes.core.database.repository.NoteRepository
import pw.cub3d.cub3_notes.core.database.repository.SortTypes
import pw.cub3d.cub3_notes.core.manager.SettingsManager
import pw.cub3d.cub3_notes.ui.nav.NewNoteNavigationController

class ArchiveViewModel @Inject constructor(
    val settingsManager: SettingsManager,
    val noteNavigationController: NewNoteNavigationController,
    val notesRepository: NoteRepository
) : ViewModel() {
    val filter = MutableLiveData(FilterType.ALL)
    val sort = MutableLiveData(SortTypes.MODIFY_DSC)

    val archivedNotes by lazy {
        notesRepository.getNotes(filter, sort, false, true).asLiveData()
    }
}
