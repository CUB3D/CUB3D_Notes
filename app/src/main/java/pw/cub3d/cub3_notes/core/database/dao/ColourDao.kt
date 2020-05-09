package pw.cub3d.cub3_notes.core.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import pw.cub3d.cub3_notes.core.database.entity.Colour

@Dao
abstract class ColourDao {
    @Insert
    abstract fun insert(colour: Colour)

    @Query("SELECT * FROM card_colour")
    abstract fun getAll(): LiveData<List<Colour>>

    @Query("DELETE FROM card_colour")
    abstract fun deleteAll()

    @Query("DELETE FROM card_colour WHERE id = :id")
    abstract fun delete(id: Long)
}