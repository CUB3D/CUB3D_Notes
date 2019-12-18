package pw.cub3d.cub3_notes.ui.newnote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pw.cub3d.cub3_notes.database.NotesDao
import pw.cub3d.cub3_notes.database.entity.Note

class NewNoteViewModel(
    private val dao: NotesDao
) : ViewModel() {

    private var note = Note()

    var type = MutableLiveData<String>()

    private fun saveNote() {
        println("Note: $note")
        GlobalScope.launch {
            dao.save(note)
        }
    }

    fun onNoteTextChanged(text: String) {
        note.text = text

        saveNote()
    }

    fun onNoteTitleChanged(text: String) {
        note.title = text

        saveNote()
    }

    fun onPin() {
        note.pinned = !note.pinned

        saveNote()
    }

    fun onArchive() {
        note.archived = true

        saveNote()
    }

    fun setNote(note: Note) {
        this.note = note
    }

    fun setNoteType(it: String) {
        note.type = it
        type.value = it
        saveNote()
    }

    fun save() {
        saveNote()
    }
}