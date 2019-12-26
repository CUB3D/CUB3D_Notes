package pw.cub3d.cub3_notes.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checkbox_entry")
data class CheckboxEntry(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var noteId: Long = 0,
    var content: String = "",
    var checked: Boolean = false,
    val indentLevel: Int = 0
)