package pw.cub3d.cub3_notes.ui.colours

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import javax.inject.Inject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pw.cub3d.cub3_notes.core.database.dao.ColourDao
import pw.cub3d.cub3_notes.core.database.entity.Colour
import pw.cub3d.cub3_notes.core.database.repository.ColourRepository

class ColoursViewModel @Inject constructor(
    private val colourDao: ColourDao,
    private val coloursRepository: ColourRepository
) : ViewModel() {
    fun addColour(color: String) {
        GlobalScope.launch { colourDao.insert(Colour(id = 0, hex_colour = color)) }
    }

    fun deleteColour(colour: Colour) {
        GlobalScope.launch { colourDao.delete(colour.id) }
    }

    val colours by lazy {
        coloursRepository.getColours().asLiveData()
    }
}
