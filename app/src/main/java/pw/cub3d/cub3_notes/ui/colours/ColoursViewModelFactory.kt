package pw.cub3d.cub3_notes.ui.colours

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pw.cub3d.cub3_notes.database.dao.ColourDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ColoursViewModelFactory @Inject constructor(
    private val colourDao: ColourDao
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) = ColoursViewModel(colourDao) as T
}