package pw.cub3d.cub3_notes.ui.newnote

import android.provider.Settings
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pw.cub3d.cub3_notes.database.dao.CheckboxEntryDao
import pw.cub3d.cub3_notes.database.dao.NotesDao
import pw.cub3d.cub3_notes.database.entity.CheckboxEntry
import pw.cub3d.cub3_notes.database.entity.Note
import java.time.ZonedDateTime

class NewNoteViewModel(
    private val dao: NotesDao,
    private val checkboxEntryDao: CheckboxEntryDao
) : ViewModel() {

    fun onCheckboxChecked(checkboxEntry: CheckboxEntry) {
        checkboxEntry.checked = !checkboxEntry.checked
        GlobalScope.launch { checkboxEntryDao.save(checkboxEntry) }
    }

    private var note = Note()

    var checkboxes: LiveData<List<CheckboxEntry>> = MutableLiveData<List<CheckboxEntry>>()

    var type = MutableLiveData<String>().apply { value = Note.TYPE_TEXT }
    var modificationTime = MutableLiveData<String>().apply { value = note.getLocalModificationTime() }

    private fun saveNote() {
        println("Note: $note")
        GlobalScope.launch {
            dao.save(note)
            checkboxEntryDao.saveAll(note.checkboxEntry)
        }
        modificationTime.value = note.getLocalModificationTime()
        checkboxes = checkboxEntryDao.getByNoteLive(note.id)
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
        checkboxes = checkboxEntryDao.getByNoteLive(note.id)
        type.value = note.type
    }

    fun setNoteType(it: String) {
        note.type = it
        type.value = it
        saveNote()
    }

    fun save() {
        saveNote()
    }

    fun addCheckbox() {
        val chk = CheckboxEntry(noteId = note.id)
        note.checkboxEntry.add(chk)
        GlobalScope.launch { checkboxEntryDao.insert(chk) }

        saveNote()
    }

    fun onCheckboxDelete(entry: CheckboxEntry) {
        GlobalScope.launch { checkboxEntryDao.delete(entry.id) }
    }

    fun onCheckboxChanged(entry: CheckboxEntry) {
        GlobalScope.launch { checkboxEntryDao.save(entry) }
    }
}