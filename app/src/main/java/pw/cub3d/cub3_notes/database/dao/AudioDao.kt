package pw.cub3d.cub3_notes.database.dao

import androidx.room.*
import pw.cub3d.cub3_notes.database.entity.AudioEntry
import pw.cub3d.cub3_notes.database.entity.ImageEntry

@Dao
abstract class AudioDao {
    @Insert
    abstract fun insert(image: AudioEntry): Long

    @Update
    abstract fun update(image: AudioEntry)

    @Query("SELECT i.* FROM audio i WHERE i.noteId = :id")
    abstract fun getByNote(id: Long): List<AudioEntry>

    @Transaction
    open fun save(entry: AudioEntry) {
        if(entry.id == 0L) {
            entry.id = insert(entry)
        } else {
            update(entry)
        }
    }

    @Query("SELECT * FROM audio")
    abstract fun getAll(): List<AudioEntry>
}