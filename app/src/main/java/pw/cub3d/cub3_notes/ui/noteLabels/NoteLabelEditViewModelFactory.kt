package pw.cub3d.cub3_notes.ui.noteLabels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pw.cub3d.cub3_notes.database.dao.LabelDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteLabelEditViewModelFactory @Inject constructor(
    private val labelDao: LabelDao
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>) = NoteLabelEditViewModel(labelDao) as T
}