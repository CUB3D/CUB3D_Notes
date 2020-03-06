package pw.cub3d.cub3_notes.ui.newnote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pw.cub3d.cub3_notes.database.dao.CheckboxEntryDao
import pw.cub3d.cub3_notes.database.dao.NotesDao
import pw.cub3d.cub3_notes.database.entity.CheckboxEntry
import pw.cub3d.cub3_notes.database.entity.Note

class NewNoteViewModel(
    private val dao: NotesDao,
    private val checkboxEntryDao: CheckboxEntryDao
) : ViewModel() {

    val title = MutableLiveData<String>()
    val text = MutableLiveData<String>()
    val modificationTime = MutableLiveData<String>()
    var type = MutableLiveData<String>()
    val checkboxes = MutableLiveData<List<CheckboxEntry>>()

    var note = Note()



    private fun saveNote() {
        note.title = title.value ?: ""
        note.text = text.value ?: ""
        note.type = type.value ?: Note.TYPE_TEXT

        println("Note: $note")
        GlobalScope.launch {
            dao.save(note)
            checkboxes.postValue(checkboxEntryDao.getByNote(note.id))
        }

        modificationTime.value = note.getLocalModificationTime()
    }

    fun onPin() {
        note.pinned = !note.pinned

        saveNote()
    }

    fun onArchive() {
        note.archived = true

        saveNote()
    }

    fun setNoteType(it: String) {
        note.type = it
        type.value = it
        saveNote()
    }

    fun save() {
//        saveNote()
//        GlobalScope.launch { checkboxEntryDao.saveAll(checkboxes.value!!) }
    }

    fun addCheckbox() {
        val chk = CheckboxEntry(noteId = note.id)
        GlobalScope.launch { checkboxEntryDao.insert(chk) }
        saveNote()
    }

    fun onCheckboxDelete(entry: CheckboxEntry) {
        println("Deleting checkbox: $entry")
        GlobalScope.launch { checkboxEntryDao.delete(entry.id) }
        saveNote()
    }

    fun saveCheckbox(checkboxEntry: CheckboxEntry) {
        println("Saving checkbox $checkboxEntry")
        GlobalScope.launch { checkboxEntryDao.update(checkboxEntry) }
    }

    fun loadNote(it: Long) {
        if(it != (-1).toLong()) {
            println("Loading note with id: $it")
            //TODO: no null
            GlobalScope.launch {
                note = dao.getNote(it)!!
                println("Loaded note: $note")
                title.postValue(note.title)
                text.postValue(note.text)
                modificationTime.postValue(note.getLocalModificationTime())
                type.postValue(note.type)

                checkboxes.postValue(checkboxEntryDao.getByNote(note.id))

                viewModelScope.launch {
                    title.observeForever { saveNote() }
                    text.observeForever { saveNote() }
                }
            }
        } else {
            title.observeForever { saveNote() }
            text.observeForever { saveNote() }
        }
    }
}