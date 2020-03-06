package pw.cub3d.cub3_notes.ui.newnote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pw.cub3d.cub3_notes.database.dao.CheckboxEntryDao
import pw.cub3d.cub3_notes.database.dao.ColourDao
import pw.cub3d.cub3_notes.database.dao.NotesDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewNoteViewModelFactory @Inject constructor(
    private val notesDao: NotesDao,
    private val checkboxEntryDao: CheckboxEntryDao,
    private val colourDao: ColourDao
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) = NewNoteViewModel(
        notesDao,
        checkboxEntryDao,
        colourDao
    ) as T
}