package pw.cub3d.cub3_notes.database.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.threeten.bp.Month
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    var title: String = "",
    var text: String = "",

    var pinned: Boolean = false,
    var archived: Boolean = false,

    var modificationTime: String = ZonedDateTime.now().format(DateTimeFormatter.ISO_ZONED_DATE_TIME),
    var type: String = TYPE_TEXT,

    @Ignore val checkboxEntry: List<CheckboxEntry> = emptyList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readArrayList(ClassLoader.getSystemClassLoader()) as List<CheckboxEntry>
    )

    fun getLocalModificationTime(): String {
        return ZonedDateTime.parse(this.modificationTime, DateTimeFormatter.ISO_ZONED_DATE_TIME)
            .toLocalDateTime().format(
            DateTimeFormatter.ofPattern("HH:mm")
        )
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(text)
        parcel.writeByte(if (pinned) 1 else 0)
        parcel.writeByte(if (archived) 1 else 0)
        parcel.writeString(modificationTime)
        parcel.writeString(type)
        parcel.writeList(checkboxEntry)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Note> {
        const val TYPE_TEXT = "text"
        const val TYPE_CHECKBOX = "checkbox"
        const val TYPE_DRAW = "draw"
        const val TYPE_AUDIO = "audio"

        override fun createFromParcel(parcel: Parcel): Note {
            return Note(parcel)
        }

        override fun newArray(size: Int): Array<Note?> {
            return arrayOfNulls(size)
        }
    }
}