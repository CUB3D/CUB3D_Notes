package pw.cub3d.cub3_notes.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

@Entity(tableName = "labels")
data class Label(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    var title: String,
    var creation_date: String = ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
)