package pw.cub3d.cub3_notes.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import pw.cub3d.cub3_notes.database.entity.Label

@Dao
abstract class LabelDao {
    @Insert
    abstract fun insert(label: Label): Long

    @Query("SELECT * FROM labels")
    abstract fun getAll(): LiveData<List<Label>>

    @Query("SELECT * FROM labels WHERE title LIKE :query")
    abstract fun findByTitle(query: String): List<Label>
}