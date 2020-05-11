package pw.cub3d.cub3_notes.core.database.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.threeten.bp.ZonedDateTime
import pw.cub3d.cub3_notes.core.database.dao.CheckboxEntryDao
import pw.cub3d.cub3_notes.core.database.dao.NotesDao
import pw.cub3d.cub3_notes.core.database.entity.NoteAndCheckboxes
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

    fun getNotes(filter: MutableLiveData<Int>, sort: MutableLiveData<Int>, pinned: MutableLiveData<Boolean>): Flow<NoteAndCheckboxes> = flow {
        filter.asFlow().collect { filter ->
            sort.asFlow().collect { sort ->
                pinned.asFlow().collect { pinned ->
                    notesDao.getNotes(pinned).collect {
                        it.sortedBy {
                            when (sort) {
                                0 -> it.note.position
                                1 -> ZonedDateTime.parse(it.note.creationTime).toEpochSecond()
                                2 -> ZonedDateTime.parse(it.note.modificationTime).toEpochSecond()
                                else -> it.note.id
                            }
                        }.forEach { emit(it) }
                    }
                }
            }
        }
    }

}