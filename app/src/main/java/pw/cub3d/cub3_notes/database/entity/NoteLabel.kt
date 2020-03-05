package pw.cub3d.cub3_notes.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_label")
data class NoteLabel(
    @PrimaryKey(autoGenerate = true)
    var id: Long,

    var note_id: Long,
    var label_id: Long
)