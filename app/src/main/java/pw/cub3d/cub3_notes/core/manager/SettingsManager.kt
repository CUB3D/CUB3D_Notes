package pw.cub3d.cub3_notes.core.manager

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
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

    private val _theme = MutableLiveData(Themes.SYSTEM)
    val theme: LiveData<Themes> = _theme

    fun setTheme(theme: Themes) {
        _theme.postValue(theme)
        sharedPrefs.edit().apply {
            putInt("THEME", theme.id)
            apply()
        }
    }

    private val _sidenav = MutableLiveData(true)
    val sideNavEnabled: LiveData<Boolean> = _sidenav

    fun setSideNavEnabled(b: Boolean) {
        _sidenav.postValue(b)
        sharedPrefs.edit().apply {
            putBoolean("SIDENAV_ENABLED", b)
            apply()
        }
    }

    private val _toolbar = MutableLiveData<Boolean>()
    val toolbarEnabled: LiveData<Boolean> = _toolbar

    fun setToolbarEnabled(b: Boolean) {
        _toolbar.postValue(b)
        sharedPrefs.edit().apply {
            putBoolean("TOOLBAR_ENABLED", b)
            apply()
        }
    }

    private val _quicknote = MutableLiveData(true)
    val quickNoteEnabled: LiveData<Boolean> = _quicknote

    fun setQuickNoteEnabled(b: Boolean) {
        _quicknote.postValue(b)
        sharedPrefs.edit().apply {
            putBoolean("QUICKNOTE_ENABLED", b)
            apply()
        }
    }

    init {
        noteLayout.postValue(Layouts.valueOf(sharedPrefs.getString("NOTE_LAYOUT", Layouts.GRID.name)!!))
        _theme.postValue(sharedPrefs.getInt("THEME", Themes.SYSTEM.id).let { id -> Themes.values().find { it.id == id } })
        _sidenav.postValue(sharedPrefs.getBoolean("SIDENAV_ENABLED", false))
        _toolbar.postValue(sharedPrefs.getBoolean("TOOLBAR_ENABLED", false))
        _quicknote.postValue(sharedPrefs.getBoolean("QUICKNOTE_ENABLED", false))
    }
}

enum class Layouts(val grid_size: Int) {
    LIST(1),
    GRID(2)
}

enum class Themes(val id: Int, val nightMode: Int) {
    SYSTEM(0, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM),
    LIGHT(1, AppCompatDelegate.MODE_NIGHT_NO),
    DARK(2, AppCompatDelegate.MODE_NIGHT_YES)
}
