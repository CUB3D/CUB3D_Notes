package pw.cub3d.cub3_notes.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "checkbox_entry")
@JsonClass(generateAdapter = true)
data class CheckboxEntry(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var noteId: Long = 0,
    var content: String = "",
    var checked: Boolean = false,
    val indentLevel: Int = 0,
    var position: Int = 1
)