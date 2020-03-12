package pw.cub3d.cub3_notes.database.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NoteAndCheckboxes(
    @Embedded
    val note: Note,

    @Relation(parentColumn = "id", entityColumn = "noteId")
    val checkboxes: List<CheckboxEntry>,

    @Relation(parentColumn = "id", entityColumn = "id", associateBy = Junction(NoteLabel::class, parentColumn = "note_id", entityColumn = "label_id"))
    val labels: List<Label>,

    @Relation(parentColumn = "id", entityColumn = "noteId")
    val images: List<ImageEntry>
)