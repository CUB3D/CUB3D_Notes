package pw.cub3d.cub3_notes

import android.content.Context
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsManager @Inject constructor(
    private val context: Context
) {
    private val sharedPrefs = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

    val noteLayout = MutableLiveData(Layouts.GRID).apply {
        observeForever {
            sharedPrefs.edit().apply {
                putString("NOTE_LAYOUT", it.name)
                apply()
            }
        }
    }

    init {
        noteLayout.postValue(Layouts.valueOf(sharedPrefs.getString("NOTE_LAYOUT", Layouts.GRID.name)!!))
    }
}

enum class Layouts(val grid_size: Int) {
    LIST(1),
    GRID(2)
}