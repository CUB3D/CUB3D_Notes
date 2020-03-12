package pw.cub3d.cub3_notes.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import pw.cub3d.cub3_notes.database.entity.Label
import pw.cub3d.cub3_notes.database.entity.NoteLabel

@Dao
abstract class LabelDao {
    @Insert
    abstract fun insert(label: Label): Long

    @Query("SELECT * FROM labels")
    abstract fun getAll(): LiveData<List<Label>>

    @Query("SELECT * FROM labels WHERE title LIKE :query")
    abstract fun findByTitle(query: String): List<Label>

    @Query("SELECT l.* FROM note_label nl INNER JOIN labels l on l.id = nl.label_id WHERE nl.note_id = :noteId AND l.title LIKE :query")
    abstract fun findByTitleAndNote(noteId: Long, query: String): List<Label>

    @Query("SELECT l.* FROM note_label nl INNER JOIN labels l on l.id = nl.label_id WHERE nl.note_id = :noteId")
    abstract fun findByNote(noteId: Long): LiveData<List<Label>>

    @Insert
    abstract fun insert(noteLabel: NoteLabel)

    @Query("DELETE FROM note_label WHERE note_id = :noteId AND label_id = :labelId")
    abstract fun deleteNoteLabel(noteId: Long, labelId: Long)
}