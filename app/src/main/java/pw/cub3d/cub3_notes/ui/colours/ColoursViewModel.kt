package pw.cub3d.cub3_notes.ui.colours

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pw.cub3d.cub3_notes.database.dao.ColourDao
import pw.cub3d.cub3_notes.database.entity.Colour

class ColoursViewModel(
    private val colourDao: ColourDao
): ViewModel() {
    fun addColour(color: String) {
        GlobalScope.launch { colourDao.insert(Colour(id=0, hex_colour = color)) }
    }

    fun deleteColour(colour: Colour) {
        GlobalScope.launch { colourDao.delete(colour.id) }
    }

    val colours = colourDao.getAll()
}
