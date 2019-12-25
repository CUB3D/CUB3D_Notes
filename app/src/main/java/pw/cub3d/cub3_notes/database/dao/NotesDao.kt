package pw.cub3d.cub3_notes.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import pw.cub3d.cub3_notes.database.entity.Note

@Dao
abstract class NotesDao {
    @Query("SELECT * FROM notes WHERE notes.pinned = 0 AND archived = 0")
    abstract fun getAllUnpinnedNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM notes WHERE notes.pinned = 1 AND archived = 0")
    abstract fun getAllPinnedNotes(): LiveData<List<Note>>

    @Insert
    abstract fun insert(note: Note): Long

    @Update
    abstract fun updateNote(note: Note)

    fun save(note: Note) {
        if(note.title.isEmpty() && note.text.isEmpty() && note.checkboxEntry.isEmpty()) {
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
