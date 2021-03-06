package pw.cub3d.cub3_notes.core.database.repository

import androidx.lifecycle.asFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import pw.cub3d.cub3_notes.core.database.dao.ColourDao
import pw.cub3d.cub3_notes.core.database.entity.Colour

@Singleton
class ColourRepository @Inject constructor(
    private val colourDao: ColourDao
) {
    fun getColours(): Flow<List<Colour>> = flow {
        colourDao.getAll().asFlow().collect {
            emit(it)
        }
    }
}
