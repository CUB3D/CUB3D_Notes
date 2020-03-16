package pw.cub3d.cub3_notes.ui.newnote

import android.text.util.Linkify
import androidx.lifecycle.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import pw.cub3d.cub3_notes.database.dao.CheckboxEntryDao
import pw.cub3d.cub3_notes.database.dao.ColourDao
import pw.cub3d.cub3_notes.database.dao.ImageDao
import pw.cub3d.cub3_notes.database.dao.NotesDao
import pw.cub3d.cub3_notes.database.entity.*

class NewNoteViewModel(
    private val dao: NotesDao,
    private val checkboxEntryDao: CheckboxEntryDao,
    private val colourDao: ColourDao,
    private val imagesDao: ImageDao
) : ViewModel() {
    val defaultNoteColours = colourDao.getAll()

    var title = MutableLiveData<String>()
    var text = MutableLiveData<String>()

    var noteAndCheckboxes: LiveData<NoteAndCheckboxes>? = null

    lateinit var type: LiveData<String>
    lateinit var pinned: LiveData<Boolean>
    lateinit var checkboxes: LiveData<List<CheckboxEntry>>
    lateinit var images: LiveData<List<ImageEntry>>
    lateinit var modificationTime: LiveData<String>


    var noteId: Long? = null

    fun onPin() {
        GlobalScope.launch { dao.pinNote(noteId!!, !pinned.value!!) }
    }

    fun onArchive() {
        GlobalScope.launch { dao.archiveNote(noteId!!, true) }
    }

    fun setNoteType(it: String) {
        GlobalScope.launch { dao.setType(noteId!!, it) }
    }

    fun addCheckbox() {
        GlobalScope.launch { checkboxEntryDao.insert(CheckboxEntry(noteId = noteId!!)) }
    }

    fun onCheckboxDelete(entry: CheckboxEntry) {
        GlobalScope.launch { checkboxEntryDao.delete(entry.id) }
    }

    fun saveCheckbox(checkboxEntry: CheckboxEntry) {
        GlobalScope.launch { checkboxEntryDao.update(checkboxEntry) }
    }

    fun loadNote(it: Long) = GlobalScope.async {
        noteId = it

        title = MutableLiveData(dao.getNoteTitle(it))
        text = MutableLiveData(dao.getNoteContent(it))

        noteAndCheckboxes = dao.getNoteLive(it)

        checkboxes = Transformations.map(noteAndCheckboxes!!) { it.checkboxes }
        type = Transformations.map(noteAndCheckboxes!!) { it.note.type }
        images = Transformations.map(noteAndCheckboxes!!) { it.images }
        modificationTime = Transformations.map(noteAndCheckboxes!!) { it.note.getLocalModificationTime() }
        pinned = Transformations.map(noteAndCheckboxes!!) { it.note.pinned }
    }

    fun setNoteColour(hexColour: String) {
        GlobalScope.launch { dao.setNoteColour(noteId!!, hexColour) }
    }

    fun addImage(imagePath: String) {
        GlobalScope.launch { imagesDao.save(ImageEntry(0, noteId!!, imagePath)) }
    }

    fun newNote() = GlobalScope.async {
        noteId = dao.insert(Note())

        noteAndCheckboxes = dao.getNoteLive(noteId!!)

        checkboxes = Transformations.map(noteAndCheckboxes!!) { it.checkboxes }
        type = Transformations.map(noteAndCheckboxes!!) { it.note.type }
        images = Transformations.map(noteAndCheckboxes!!) { it.images }
        modificationTime = Transformations.map(noteAndCheckboxes!!) { it.note.getLocalModificationTime() }
        pinned = Transformations.map(noteAndCheckboxes!!) { it.note.pinned }
    }

    fun onTitleChange(title: String) {
        println("Title change: $title")
        GlobalScope.launch { dao.setTitle(noteId!!, title) }
    }

    fun onTextChange(text: String) {
        println("Text change")
        GlobalScope.launch { dao.setText(noteId!!, text) }
    }
}