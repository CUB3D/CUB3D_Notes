package pw.cub3d.cub3_notes.ui.nav

import android.os.Bundle
import androidx.navigation.NavController
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.entity.Note
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewNoteNavigationController @Inject constructor() {
    fun navigateNewNote(nav: NavController, type: String = Note.TYPE_TEXT) {
        nav.navigate(R.id.nav_new_note, Bundle().apply {
            putString(KEY_NOTE_TYPE, type)
            putBoolean(KEY_NEW_NOTE, true)
        })
    }

    fun navigateNewNoteWithImage(nav: NavController, imagePath: String) {
        nav.navigate(R.id.nav_new_note, Bundle().apply {
            putString(KEY_NOTE_TYPE, Note.TYPE_IMAGE)
            putString(KEY_NOTE_IMAGE_PATH, imagePath)
            putBoolean(KEY_NEW_NOTE, true)
        })
    }

    fun editNote(nav: NavController, note: Note) {
        editNote(nav, note.id)
    }

    fun editNote(nav: NavController, noteId: Long) {
        nav.navigate(R.id.nav_new_note, Bundle().apply {
            putLong(KEY_NOTE, noteId)
        })
    }

    companion object {
        const val KEY_NOTE_TYPE = "NOTE_TYPE"
        const val KEY_NOTE = "NOTE"
        const val KEY_NOTE_IMAGE_PATH = "NOTE_IMAGE_PATH"
        const val KEY_NEW_NOTE = "NOTE_NEW"
    }
}