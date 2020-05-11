package pw.cub3d.cub3_notes.core.database.entity

import android.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

@Entity(tableName = "notes")
@JsonClass(generateAdapter = true)
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    var title: String = "",
    var text: String = "",

    var pinned: Boolean = false,
    var archived: Boolean = false,

    var modificationTime: String = ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME),
    var type: String = TYPE_TEXT,

    var timeReminder: String? = null,

    var colour: String? = null,

    var deletionTime: String? = null,

    var position: Long = 0,

    var creationTime: String = ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME),
    var viewTime: String = ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME)

) {
    fun getLocalModificationTime(): String {
        return try {
            ZonedDateTime.parse(this.modificationTime, DateTimeFormatter.ISO_ZONED_DATE_TIME)
                .toLocalDateTime().format(
                    DateTimeFormatter.ofPattern("HH:mm")
                )
        } catch (e: Exception) {
            e.printStackTrace()

            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        }
    }

    fun getColourId() = colour?.let {Color.parseColor(it) } ?: Color.TRANSPARENT

    fun updateModificationTime() {
        modificationTime = ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
    }

    fun getReminderTimeZoned() = timeReminder?.let { ZonedDateTime.parse(timeReminder) }

    fun formattedReminderTime() = getReminderTimeZoned()?.format(DateTimeFormatter.ofPattern("dd MMM YYYY, HH:mm"))

    companion object {
        const val TYPE_TEXT = "TEXT"
        const val TYPE_CHECKBOX = "CHECK"
        const val TYPE_DRAW = "DRAW"
        const val TYPE_AUDIO = "AUDIO"
        const val TYPE_IMAGE = "IMAGE"
        const val TYPE_VIDEO = "VIDEO"
    }
}