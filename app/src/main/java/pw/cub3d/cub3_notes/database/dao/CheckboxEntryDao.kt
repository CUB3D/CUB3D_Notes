package pw.cub3d.cub3_notes.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import pw.cub3d.cub3_notes.database.entity.CheckboxEntry
import pw.cub3d.cub3_notes.database.entity.Note

@Dao
abstract class CheckboxEntryDao {
    @Insert
    abstract fun insert(data: CheckboxEntry): Long

    @Query("SELECT * FROM checkbox_entry WHERE noteId = :noteId")
    abstract fun getByNote(noteId: Long): List<CheckboxEntry>
    @Query("SELECT * FROM checkbox_entry WHERE noteId = :noteId")
    abstract fun getByNoteLive(noteId: Long): LiveData<List<CheckboxEntry>>
}
