package pw.cub3d.cub3_notes.ui.nav

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.entity.Note
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewNoteNavigationController @Inject constructor() {
    fun navigateNewNote(nav: NavController, type: String = NOTE_TYPE_TEXT) {
        nav.navigate(R.id.nav_new_note, Bundle().apply {
            putString(KEY_NOTE_TYPE, type)
        })
    }

    fun editNote(nav: NavController, note: Note) {
        nav.navigate(R.id.nav_new_note, Bundle().apply {
            putParcelable(KEY_NOTE, note)
        })
    }

    companion object {
        const val KEY_NOTE_TYPE = "NOTE_TYPE"
        const val KEY_NOTE = "NOTE"

        const val NOTE_TYPE_TEXT = "TEXT"
        const val NOTE_TYPE_CHECK = "CHECK"
        const val NOTE_TYPE_DRAW = "DRAW"
        const val NOTE_TYPE_AUDIO = "AUDIO"
        const val NOTE_TYPE_IMAGE = "IMAGE"

    }
}