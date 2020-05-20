package pw.cub3d.cub3_notes.core.manager

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton
import pw.cub3d.cub3_notes.R

@Singleton
class AutoCompleteManager @Inject constructor(
    private val context: Context
) {
    val food = context.resources.openRawResource(R.raw.food).bufferedReader().readText().split("\n")

    init {
        println("Got ${food.size} entries for food")
    }
}
