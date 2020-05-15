package pw.cub3d.cub3_notes.ui.nav

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.core.database.entity.Note
import pw.cub3d.cub3_notes.databinding.NoteEntryBinding
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

    fun editNote(nav: NavController, note: Note, binding: NoteEntryBinding) {
        editNote(nav, note.id,
            binding.noteTitle to "${note.id}_title",
            binding.noteText to "${note.id}_text"
        )
    }

    fun editNote(nav: NavController, note: Note, vararg sharedElements: Pair<View, String>) {
        editNote(nav, note.id, *sharedElements)
    }

    fun editNote(nav: NavController, noteId: Long, vararg sharedElements: Pair<View, String>) {
        nav.navigate(R.id.nav_new_note, Bundle().apply {
            putLong(KEY_NOTE, noteId)
        }, null, FragmentNavigatorExtras(*sharedElements))
    }

    fun navigateNewNoteWithAudio(nav: NavController, name: String) {
        nav.navigate(R.id.nav_new_note, Bundle().apply {
            putString(KEY_NOTE_TYPE, Note.TYPE_AUDIO)
            putString(KEY_NOTE_AUDIO_PATH, name)
            putBoolean(KEY_NEW_NOTE, true)
        })
    }

    fun navigateNewNoteWithVideo(nav: NavController, videoPath: String) {
        nav.navigate(R.id.nav_new_note, Bundle().apply {
            putString(KEY_NOTE_TYPE, Note.TYPE_VIDEO)
            putString(KEY_NOTE_VIDEO_PATH, videoPath)
            putBoolean(KEY_NEW_NOTE, true)
        })
    }

    companion object {
        const val KEY_NOTE_TYPE = "NOTE_TYPE"
        const val KEY_NOTE = "NOTE"
        const val KEY_NOTE_IMAGE_PATH = "NOTE_IMAGE_PATH"
        const val KEY_NOTE_AUDIO_PATH = "NOTE_AUDIO_PATH"
        const val KEY_NOTE_VIDEO_PATH = "NOTE_VIDEO_PATH"
        const val KEY_NEW_NOTE = "NOTE_NEW"
    }
}