package pw.cub3d.cub3_notes.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "image", foreignKeys = [ForeignKey(entity = Note::class, parentColumns = ["id"], childColumns = ["noteId"])])
data class ImageEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val noteId: Long = 0,

    val imageName: String = ""
)