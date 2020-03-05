package pw.cub3d.cub3_notes.ui.labelEdit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pw.cub3d.cub3_notes.database.dao.LabelDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LabelEditViewModelFactory @Inject constructor(
    private val labelDao: LabelDao
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) = LabelEditViewModel(labelDao) as T
}