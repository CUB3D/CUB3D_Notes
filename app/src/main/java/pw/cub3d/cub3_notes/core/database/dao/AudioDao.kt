package pw.cub3d.cub3_notes.core.database.dao

import androidx.room.*
import pw.cub3d.cub3_notes.core.database.entity.AudioEntry

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