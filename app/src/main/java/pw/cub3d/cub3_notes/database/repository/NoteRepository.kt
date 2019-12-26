package pw.cub3d.cub3_notes.database.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import pw.cub3d.cub3_notes.database.dao.CheckboxEntryDao
import pw.cub3d.cub3_notes.database.dao.NotesDao
import pw.cub3d.cub3_notes.database.entity.Note
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val notesDao: NotesDao,
    private val checkboxEntryDao: CheckboxEntryDao
) {
    fun getAllUnpinnedNotes(): LiveData<List<Note>> {
        return Transformations.map(notesDao.getAllUnpinnedNotes()) {
            it.map { it.getFullNote() }
        }
    }

    fun getAllPinnedNotes(): LiveData<List<Note>> {
        return Transformations.map(notesDao.getAllPinnedNotes()) {
            it.map { it.getFullNote() }
        }
    }
}