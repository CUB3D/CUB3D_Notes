package pw.cub3d.cub3_notes.core.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import pw.cub3d.cub3_notes.core.database.entity.Note
import pw.cub3d.cub3_notes.core.database.entity.NoteAndCheckboxes

@Dao
abstract class NotesDao {
    @Transaction
    @Query("SELECT * FROM notes")
    abstract fun getNoteSearchResults(): LiveData<List<NoteAndCheckboxes>>

    @Transaction
    @Query("SELECT * FROM notes WHERE archived = :archived")
    abstract fun getNotes(archived: Boolean): Flow<List<NoteAndCheckboxes>>

    @Transaction
    @Query("SELECT * FROM notes WHERE notes.archived = 1 AND deletionTime IS NULL")
    abstract fun getAllArchivedNotes(): LiveData<List<NoteAndCheckboxes>>

    @Transaction
    @Query("SELECT * FROM notes WHERE deletionTime IS NOT NULL")
    abstract fun getDeletedNotes(): LiveData<List<NoteAndCheckboxes>>

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

    @Query("SELECT title from notes WHERE id = :noteId")
    abstract fun getNoteTitle(noteId: Long): String

    @Query("SELECT text from notes WHERE id = :noteId")
    abstract fun getNoteContent(noteId: Long): String

    @Transaction
    @Query("SELECT * FROM notes WHERE id = :noteId")
    abstract fun getNoteLive(noteId: Long): LiveData<NoteAndCheckboxes>

    @Query("UPDATE notes SET archived = :state WHERE id = :id")
    abstract fun archiveNote(id: Long, state: Boolean)

    @Query("UPDATE notes SET pinned = :state WHERE id = :id")
    abstract fun pinNote(id: Long, state: Boolean)

    @Query("UPDATE notes SET colour = :colour, modificationTime = :time WHERE id = :id")
    abstract fun setNoteColour(id: Long, colour: String, time: String = currentTime())

    @Query("UPDATE notes SET type = :type WHERE id = :id")
    abstract fun setType(id: Long, type: String)

    @Query("UPDATE notes SET title = :title, modificationTime = :time WHERE id = :id")
    abstract fun setTitle(id: Long, title: String, time: String = currentTime())

    @Query("UPDATE notes SET text = :text, modificationTime = :time WHERE id = :id")
    abstract fun setText(id: Long, text: String, time: String = currentTime())

    fun currentTime() = ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME)

    @Query("UPDATE notes SET deletionTime = :time WHERE id = :id")
    abstract fun setDeletionTime(id: Long, time: String? = currentTime())

    @Query("UPDATE notes set deletionTime = NULL WHERE id = :id")
    abstract fun restoreNote(id: Long)

    @Query("UPDATE notes set timeReminder = :time WHERE id = :id")
    abstract fun setNoteReminder(id: Long, time: String)

    @Query("DELETE FROM notes WHERE id = :id")
    abstract fun deletePermanently(id: Long)

    @Query("UPDATE notes set position = :position WHERE id = :id")
    abstract fun setNotePosition(id: Long, position: Long)
}
