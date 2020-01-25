package pw.cub3d.cub3_notes.database.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NoteAndCheckboxes(
    @Embedded
    val note: Note,

    @Relation(parentColumn = "id", entityColumn = "noteId")
    val checkboxes: List<CheckboxEntry>
)