package pw.cub3d.cub3_notes.database.dao

import androidx.room.*
import pw.cub3d.cub3_notes.database.entity.ImageEntry

@Dao
abstract class ImageDao {
    @Insert
    abstract fun insert(image: ImageEntry): Long

    @Update
    abstract fun update(image: ImageEntry)

    @Query("SELECT i.* FROM image i WHERE i.noteId = :id")
    abstract fun getByNote(id: Long): List<ImageEntry>

    @Transaction
    open fun save(imageEntry: ImageEntry) {
        if(imageEntry.id == 0L) {
            imageEntry.id = insert(imageEntry)
        } else {
            update(imageEntry)
        }
    }

    @Query("SELECT * FROM image")
    abstract fun getAll(): List<ImageEntry>
}