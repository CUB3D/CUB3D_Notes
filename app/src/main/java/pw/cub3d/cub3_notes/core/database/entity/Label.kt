package pw.cub3d.cub3_notes.core.database.entity

import android.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

@Entity(tableName = "labels")
@JsonClass(generateAdapter = true)
data class Label(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    var title: String,
    var creation_date: String = ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME),

    var colour: String? = null
) {
    fun getColourId() = colour?.let { Color.parseColor(it) } ?: Color.TRANSPARENT
    fun getColourIdChip() = colour?.let { Color.parseColor(it) } ?: Color.LTGRAY
}
