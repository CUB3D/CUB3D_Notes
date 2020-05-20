package pw.cub3d.cub3_notes.core.database.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import org.threeten.bp.ZonedDateTime
import pw.cub3d.cub3_notes.core.database.dao.CheckboxEntryDao
import pw.cub3d.cub3_notes.core.database.dao.NotesDao
import pw.cub3d.cub3_notes.core.database.entity.NoteAndCheckboxes

@Singleton
class NoteRepository @Inject constructor(
    private val notesDao: NotesDao,
    private val checkboxEntryDao: CheckboxEntryDao
) {

    suspend fun getAllNotes() = notesDao.getAllNotes()

    suspend fun archiveNote(noteId: Long, state: Boolean) = notesDao.archiveNote(noteId, state)

    suspend fun setNotePosition(id: Long, position: Long) = notesDao.setNotePosition(id, position)

    fun getNotes(filter: LiveData<FilterType>, sort: LiveData<SortTypes>, pinnedOnly: Boolean, archived: Boolean): Flow<List<NoteAndCheckboxes>> = flow {

            filter.asFlow().combine(sort.asFlow()) { filter, sort ->
                NoteAction(filter, sort)
            }.combine(notesDao.getNotes(archived)) { a, n ->
                Temp(a, n)
            }.collect { t ->

                val notes = t.notes
                val action = t.a

                val sort = action.sort
                val filter = action.filter

                notes.sortedBy {
                    when (sort) {
                        SortTypes.MANUAL -> it.note.position
                        SortTypes.CREATED_ASC, SortTypes.CREATED_DSC -> ZonedDateTime.parse(
                            it.note.creationTime
                        ).toEpochSecond()
                        SortTypes.MODIFY_ASC, SortTypes.MODIFY_DSC -> it.note.modificationTime.takeIf { it.isNotBlank() }?.let { ZonedDateTime.parse(it).toEpochSecond() } ?: ZonedDateTime.now().toEpochSecond()
                        SortTypes.VIEW_ASC, SortTypes.VIEW_DSC -> it.note.viewTime.takeIf { it.isNotBlank() }?.let { ZonedDateTime.parse(it).toEpochSecond() } ?: ZonedDateTime.now().toEpochSecond()
                        else -> it.note.id
                    }
                }
                    .run {
                        if (sort in arrayOf(
                                SortTypes.TITLE_ALPHABETICAL,
                                SortTypes.TITLE_REVERSE_ALPHABETICAL
                            )
                        ) sortedBy { it.note.title } else this
                    }
                    .run { if (sort.reverse) reversed() else this }
                    .filter {
                        when (filter) {
                            FilterType.ALL -> true
                            FilterType.REMINDERS -> !it.note.timeReminder.isNullOrEmpty()
                            FilterType.TAGGED -> it.labels.isNotEmpty()
                            FilterType.AUDIO -> it.audioClips.isNotEmpty()
                            FilterType.VIDEO -> it.videos.isNotEmpty()
                            FilterType.IMAGE -> it.images.isNotEmpty()
                            FilterType.CHECKBOX -> it.checkboxes.isNotEmpty()
                        }
                    }
                    .filter { if (pinnedOnly) it.note.pinned else true }.let { emit(it) }
            }
        }
    }

data class NoteAction(
    val filter: FilterType,
    val sort: SortTypes
)

data class Temp(
    val a: NoteAction,
    val notes: List<NoteAndCheckboxes>
)

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
