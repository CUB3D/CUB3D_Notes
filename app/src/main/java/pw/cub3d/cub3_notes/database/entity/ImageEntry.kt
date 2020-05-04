package pw.cub3d.cub3_notes.database.entity

import android.content.Context
import android.util.Base64
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import java.io.File

@Entity(tableName = "image", foreignKeys = [ForeignKey(entity = Note::class, parentColumns = ["id"], childColumns = ["noteId"])])
@JsonClass(generateAdapter = true)
data class ImageEntry(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    val noteId: Long = 0,

    val imageName: String = ""
) {
    fun getFile(context: Context) = File(File(context.filesDir, "images/"), imageName)
    fun toBase64(context: Context) = Base64.encodeToString(getFile(context).readBytes(), Base64.URL_SAFE.or(Base64.NO_WRAP))!!
}