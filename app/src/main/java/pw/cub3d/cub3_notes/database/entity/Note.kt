package pw.cub3d.cub3_notes.database.entity

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Month
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

    var colour: String = "#ffffff",

    var deletionTime: String? = ""

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString()!!
    //TODO: delet
    )

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

    fun getColourId() = Color.parseColor(colour)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(text)
        parcel.writeByte(if (pinned) 1 else 0)
        parcel.writeByte(if (archived) 1 else 0)
        parcel.writeString(modificationTime)
        parcel.writeString(type)
        parcel.writeString(timeReminder)
        parcel.writeString(colour)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun updateModificationTime() {
        modificationTime = ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME)
    }

    companion object CREATOR : Parcelable.Creator<Note> {
        const val TYPE_TEXT = "TEXT"
        const val TYPE_CHECKBOX = "CHECK"
        const val TYPE_DRAW = "DRAW"
        const val TYPE_AUDIO = "AUDIO"
        const val TYPE_IMAGE = "IMAGE"

        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }
    }
}