package pw.cub3d.cub3_notes.core.database.repository

import pw.cub3d.cub3_notes.core.database.dao.CheckboxEntryDao
import pw.cub3d.cub3_notes.core.database.dao.NotesDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val notesDao: NotesDao,
    private val checkboxEntryDao: CheckboxEntryDao
) {
    fun getAllUnpinnedNotes() = notesDao.getAllUnpinnedNotes()
    fun getAllUnpinnedReminders() = notesDao.getAllUnpinnedReminders()

    fun getAllPinnedNotes() = notesDao.getAllPinnedNotes()
    fun getAllPinnedReminders() = notesDao.getAllPinnedReminders()

    suspend fun getAllNotes() = notesDao.getAllNotes()

    suspend fun archiveNote(noteId: Long, state: Boolean) = notesDao.archiveNote(noteId, state)

    suspend fun setNotePosition(id: Long, position: Long) = notesDao.setNotePosition(id, position)

}