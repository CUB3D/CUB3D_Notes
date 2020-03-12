package pw.cub3d.cub3_notes.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "note_label", indices = [
    Index("note_id", "label_id", name = "index_note_label")
])
data class NoteLabel(
    @PrimaryKey(autoGenerate = true)
    var id: Long,


    var note_id: Long,
    var label_id: Long
)