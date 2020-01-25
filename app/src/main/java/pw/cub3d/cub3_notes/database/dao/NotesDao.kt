package pw.cub3d.cub3_notes.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import pw.cub3d.cub3_notes.database.entity.Note
import pw.cub3d.cub3_notes.database.entity.NoteAndCheckboxes

@Dao
abstract class NotesDao {
    @Transaction
    @Query("SELECT * FROM notes WHERE notes.pinned = 0 AND archived = 0")
    abstract fun getAllUnpinnedNotes(): LiveData<List<NoteAndCheckboxes>>

    @Transaction
    @Query("SELECT * FROM notes WHERE notes.pinned = 1 AND archived = 0")
    abstract fun getAllPinnedNotes(): LiveData<List<NoteAndCheckboxes>>

    @Transaction
    @Query("SELECT * FROM notes")
    abstract suspend fun getAllNotes(): List<NoteAndCheckboxes>

    @Insert
    abstract fun insert(note: Note): Long

    @Update
    abstract fun updateNote(note: Note)

    fun save(note: Note) {
        if(note.title.isEmpty() && note.text.isEmpty() && note.type == Note.TYPE_TEXT) {
            println("Attempt to save empty note")
            return
        }

        note.updateModificationTime()

        // If the note dose't exist yet
        if(note.id == 0L) {
            println("Inserted note: $note")
            note.id = insert(note)
        } else {
            updateNote(note)
            println("Updated note: $note")
        }
    }

    @Query("SELECT * FROM notes WHERE id = :noteId")
    abstract fun getNote(noteId: Long): Note?
}
