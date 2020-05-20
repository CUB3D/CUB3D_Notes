package pw.cub3d.cub3_notes.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "video", foreignKeys = [ForeignKey(entity = Note::class, parentColumns = ["id"], childColumns = ["noteId"])])
@JsonClass(generateAdapter = true)
data class VideoEntry(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    val noteId: Long = 0,

    val fileName: String = ""
)
