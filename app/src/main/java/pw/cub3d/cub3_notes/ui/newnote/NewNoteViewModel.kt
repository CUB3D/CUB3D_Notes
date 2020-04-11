package pw.cub3d.cub3_notes.ui.newnote

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.text.util.Linkify
import androidx.lifecycle.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import pw.cub3d.cub3_notes.ReminderBroadcastReciever
import pw.cub3d.cub3_notes.database.dao.CheckboxEntryDao
import pw.cub3d.cub3_notes.database.dao.ColourDao
import pw.cub3d.cub3_notes.database.dao.ImageDao
import pw.cub3d.cub3_notes.database.dao.NotesDao
import pw.cub3d.cub3_notes.database.entity.*

class NewNoteViewModel(
    private val dao: NotesDao,
    private val checkboxEntryDao: CheckboxEntryDao,
    private val colourDao: ColourDao,
    private val imagesDao: ImageDao,
    private val context: Context
) : ViewModel() {
    val defaultNoteColours = colourDao.getAll().map {
        it + Colour(-1, "")
    }

    var title = MutableLiveData<String>()
    var text = MutableLiveData<String>()

    var noteAndCheckboxes: LiveData<NoteAndCheckboxes>? = null

    lateinit var type: LiveData<String>
    lateinit var pinned: LiveData<Boolean>
    lateinit var checkboxes: LiveData<List<CheckboxEntry>>
    lateinit var images: LiveData<List<ImageEntry>>
    lateinit var modificationTime: LiveData<String>
    lateinit var colour: LiveData<Int>


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
        colour = noteAndCheckboxes!!.map { Color.parseColor(it.note.colour) }
    }

    fun setNoteColour(hexColour: String) {
        GlobalScope.launch { dao.setNoteColour(noteId!!, hexColour) }
    }

    fun addImage(imagePath: String) {
        GlobalScope.launch { imagesDao.save(ImageEntry(0, noteId!!, imagePath)) }
    }

    fun newNote() = GlobalScope.async {
        noteId = dao.insert(Note())

        println("Got note id: $noteId")

        println("All notes: ${dao.getAllNotes()}")

        noteAndCheckboxes = dao.getNoteLive(noteId!!)

        checkboxes = Transformations.map(noteAndCheckboxes!!) { it.checkboxes }
        type = Transformations.map(noteAndCheckboxes!!) { it.note.type }
        images = Transformations.map(noteAndCheckboxes!!) { it.images }
        modificationTime = Transformations.map(noteAndCheckboxes!!) { it.note.getLocalModificationTime() }
        pinned = Transformations.map(noteAndCheckboxes!!) { it.note.pinned }
        colour = noteAndCheckboxes!!.map { Color.parseColor(it.note.colour) }

    }

    fun onTitleChange(title: String) {
        println("Title change: $title")
        GlobalScope.launch { dao.setTitle(noteId!!, title) }
    }

    fun onTextChange(text: String) {
        println("Text change")
        GlobalScope.launch { dao.setText(noteId!!, text) }
    }

    fun onDelete() {
        GlobalScope.launch { dao.deleteNote(noteId!!) }
    }

    fun addCheckbox() {
        GlobalScope.launch { checkboxEntryDao.insert(CheckboxEntry(noteId = noteId!!, position = checkboxes.value?.lastOrNull()?.position?.plus(1) ?: 1)) }
    }

    fun onCheckboxChecked(checkboxEntry: CheckboxEntry, checked: Boolean) {
        println("Checked: ${checkboxEntry}, $checked")
        checkboxEntry.checked = checked
        GlobalScope.launch { checkboxEntryDao.setChecked(checkboxEntry.id, checked) }
    }

    fun onCheckboxDelete(entry: CheckboxEntry) {
        GlobalScope.launch { checkboxEntryDao.delete(entry.id) }
    }

    fun onCheckboxTextChange(entry: CheckboxEntry, text: String) {
        GlobalScope.launch { checkboxEntryDao.setText(entry.id, text) }
    }

    fun upadateCheckboxPosition(entry: CheckboxEntry, position: Int) {
        GlobalScope.launch { checkboxEntryDao.setPosition(entry.id, position) }
    }

    fun setNoteReminder(zonedDateTime: ZonedDateTime) {
        GlobalScope.launch {
            dao.setNoteReminder(noteId!!, zonedDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))

            setAlarm(context, zonedDateTime.toEpochSecond(),
                PendingIntent.getBroadcast(context, 0, Intent(context, ReminderBroadcastReciever::class.java).apply {
                    putExtra("NOTE_ID", noteId!!)
                }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT)
            )
        }
    }

    private fun setAlarm(
        context: Context,
        time: Long,
        pendingIntent: PendingIntent
    ) {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) alarmManager[AlarmManager.RTC_WAKEUP, time] =
            pendingIntent else if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        ) else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }
}