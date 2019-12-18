package pw.cub3d.cub3_notes.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checkbox_entry")
data class CheckboxEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val noteId: Long = 0,
    val content: String = "",
    val checked: Boolean = false,
    val indentLevel: Int = 0
)