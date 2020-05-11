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

    fun getNotes(filter: MutableLiveData<FilterType>, sort: MutableLiveData<SortTypes>, pinned: Boolean, archived: Boolean): Flow<List<NoteAndCheckboxes>> = flow {
        filter.asFlow().collect { filter ->
            sort.asFlow().collect { sort ->
                    notesDao.getNotes(pinned, archived).collect {
                        it.sortedBy {
                            when (sort) {
                                SortTypes.MANUAL -> it.note.position
                                SortTypes.CREATED_ASC, SortTypes.CREATED_DSC -> ZonedDateTime.parse(
                                    it.note.creationTime
                                ).toEpochSecond()
                                SortTypes.MODIFY_ASC, SortTypes.MODIFY_DSC -> ZonedDateTime.parse(it.note.modificationTime)
                                    .toEpochSecond()
                                SortTypes.VIEW_ASC, SortTypes.VIEW_DSC -> ZonedDateTime.parse(it.note.viewTime)
                                    .toEpochSecond()
                                else -> it.note.id
                            }
                        }
                        .run { if(sort in arrayOf(SortTypes.TITLE_ALPHABETICAL, SortTypes.TITLE_REVERSE_ALPHABETICAL)) sortedBy { it.note.title } else this }
                        .run { if(sort.reverse) reversed() else this }
                        .filter {
                            when(filter) {
                                FilterType.ALL -> true
                                FilterType.REMINDERS -> !it.note.timeReminder.isNullOrEmpty()
                                FilterType.TAGGED -> it.labels.isNotEmpty()
                                FilterType.AUDIO -> it.audioClips.isNotEmpty()
                                FilterType.VIDEO -> it.videos.isNotEmpty()
                                FilterType.IMAGE -> it.images.isNotEmpty()
                                FilterType.CHECKBOX -> it.checkboxes.isNotEmpty()
                                else -> true // Should never happen
                            }
                        }.let { emit(it) }
                }
            }
        }
    }
}

enum class FilterType {
    ALL,
    REMINDERS,
    TAGGED,
    AUDIO,
    VIDEO,
    IMAGE,
    CHECKBOX
}

enum class SortTypes(val reverse: Boolean = false) {
    MANUAL,
    CREATED_ASC,
    CREATED_DSC(true),
    MODIFY_ASC,
    MODIFY_DSC(true),
    VIEW_ASC,
    VIEW_DSC(true),
    TITLE_ALPHABETICAL,
    TITLE_REVERSE_ALPHABETICAL(true),

}