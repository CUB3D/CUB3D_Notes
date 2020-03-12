package pw.cub3d.cub3_notes.database.dao

import androidx.room.Dao
import androidx.room.Insert
import pw.cub3d.cub3_notes.database.entity.ImageEntry

@Dao
abstract class ImageDao {
    @Insert
    abstract fun insert(image: ImageEntry)
}