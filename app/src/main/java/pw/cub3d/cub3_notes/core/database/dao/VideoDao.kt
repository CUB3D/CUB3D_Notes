package pw.cub3d.cub3_notes.core.database.dao

import androidx.room.*
import pw.cub3d.cub3_notes.core.database.entity.VideoEntry

@Dao
abstract class VideoDao {
    @Insert
    abstract fun insert(image: VideoEntry): Long

    @Update
    abstract fun update(image: VideoEntry)

    @Query("SELECT i.* FROM video i WHERE i.noteId = :id")
    abstract fun getByNote(id: Long): List<VideoEntry>

    @Transaction
    open fun save(entry: VideoEntry) {
        if (entry.id == 0L) {
            entry.id = insert(entry)
        } else {
            update(entry)
        }
    }

    @Query("SELECT * FROM video")
    abstract fun getAll(): List<VideoEntry>
}
