package pw.cub3d.cub3_notes.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class NoteAndCheckboxes(
    @Embedded
    val note: Note,

    @Relation(parentColumn = "id", entityColumn = "noteId")
    val checkboxes: List<CheckboxEntry>
) {
    fun getFullNote(): Note {
        return note.apply {
            checkboxEntry.addAll(checkboxes)
        }
    }
}