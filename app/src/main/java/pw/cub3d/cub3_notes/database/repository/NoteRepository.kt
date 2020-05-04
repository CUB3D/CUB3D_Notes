package pw.cub3d.cub3_notes.database.repository

import pw.cub3d.cub3_notes.database.dao.CheckboxEntryDao
import pw.cub3d.cub3_notes.database.dao.NotesDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val notesDao: NotesDao,
    private val checkboxEntryDao: CheckboxEntryDao
) {
    fun getAllUnpinnedNotes() = notesDao.getAllUnpinnedNotes()

    fun getAllPinnedNotes() = notesDao.getAllPinnedNotes()

    suspend fun getAllNotes() = notesDao.getAllNotes()
}