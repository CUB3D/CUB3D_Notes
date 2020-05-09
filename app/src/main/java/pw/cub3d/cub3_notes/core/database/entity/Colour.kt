package pw.cub3d.cub3_notes.core.database.entity

import android.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.lang.Exception

@Entity(tableName = "card_colour")
data class Colour(
    @PrimaryKey(autoGenerate = true)
    var id: Long,

    var hex_colour: String
) {
    fun getColourId() = try {Color.parseColor(hex_colour) } catch (e: Exception) {Color.BLACK}
}