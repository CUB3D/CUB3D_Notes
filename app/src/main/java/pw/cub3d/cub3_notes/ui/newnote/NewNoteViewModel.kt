package pw.cub3d.cub3_notes.ui.newnote

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import pw.cub3d.cub3_notes.ReminderBroadcastReciever
import pw.cub3d.cub3_notes.core.database.dao.*
import pw.cub3d.cub3_notes.core.database.entity.*
import pw.cub3d.cub3_notes.core.manager.StorageManager
import javax.inject.Inject

class NewNoteViewModel @Inject constructor(
    val storageManager: StorageManager,
    private val dao: NotesDao,
    private val checkboxEntryDao: CheckboxEntryDao,
    private val colourDao: ColourDao,
    private val imagesDao: ImageDao,
    private val context: Context,
    private val audioDao: AudioDao,
    private val videoDao: VideoDao
): ViewModel() {
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
    lateinit var audioClips: LiveData<List<AudioEntry>>
    lateinit var videos: LiveData<List<VideoEntry>>
    lateinit var modificationTime: LiveData<String>
    lateinit var colour: LiveData<Int>
    lateinit var deletionTime: LiveData<String?>
    lateinit var archived: LiveData<Boolean>


    var noteId: Long? = null

    fun onPin() {
        GlobalScope.launch { dao.pinNote(noteId!!, !pinned.value!!) }
    }

    fun onArchive() {
        GlobalScope.launch { dao.archiveNote(noteId!!, !archived.value!!) }
    }

    fun setNoteType(it: String) {
        GlobalScope.launch { dao.setType(noteId!!, it) }
        if(it == Note.TYPE_CHECKBOX) {
            addCheckbox()
        }
    }

    fun loadNote(it: Long) = GlobalScope.async {
        noteId = it

        title = MutableLiveData(dao.getNoteTitle(it))
        text = MutableLiveData(dao.getNoteContent(it))

        noteAndCheckboxes = dao.getNoteLive(it)

        checkboxes = noteAndCheckboxes!!.map { it.checkboxes }
        type = noteAndCheckboxes!!.map { it.note.type }
        images = noteAndCheckboxes!!.map { it.images }
        audioClips = noteAndCheckboxes!!.map { it.audioClips }
        videos = noteAndCheckboxes!!.map { it.videos }.distinctUntilChanged()
        modificationTime = noteAndCheckboxes!!.map { it.note.getLocalModificationTime() }
        pinned = noteAndCheckboxes!!.map { it.note.pinned }
        colour = noteAndCheckboxes!!.map { it.note.getColourId() }
        deletionTime = noteAndCheckboxes!!.map { it.note.deletionTime }
        archived = noteAndCheckboxes!!.map { it.note.archived }
    }

    fun setNoteColour(hexColour: String) {
        GlobalScope.launch { dao.setNoteColour(noteId!!, hexColour) }
    }

    fun addImage(imagePath: String) {
        GlobalScope.launch { imagesDao.save(ImageEntry(0, noteId!!, imagePath)) }
    }

    fun newNote() = GlobalScope.async {
        noteId = dao.insert(Note())

        dao.setNotePosition(noteId!!, noteId!!)

        println("Got note id: $noteId")

        println("All notes: ${dao.getAllNotes()}")

        noteAndCheckboxes = dao.getNoteLive(noteId!!)

        checkboxes = noteAndCheckboxes!!.map { it.checkboxes }
        type = noteAndCheckboxes!!.map { it.note.type }
        images = noteAndCheckboxes!!.map { it.images }
        audioClips = noteAndCheckboxes!!.map { it.audioClips }
        videos = noteAndCheckboxes!!.map { it.videos }.distinctUntilChanged()
        modificationTime = noteAndCheckboxes!!.map { it.note.getLocalModificationTime() }
        pinned = noteAndCheckboxes!!.map { it.note.pinned }
        colour = noteAndCheckboxes!!.map { it.note.getColourId() }
        deletionTime = noteAndCheckboxes!!.map { it.note.deletionTime }
        archived = noteAndCheckboxes!!.map { it.note.archived }
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
        GlobalScope.launch { dao.setDeletionTime(noteId!!) }
    }

    fun addCheckbox() {
        GlobalScope.launch { checkboxEntryDao.insert(CheckboxEntry(noteId = noteId!!, position = (checkboxes.value?.maxBy { it.position }?.position?.plus(1) ?: 1))) }
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

    fun upadateCheckboxPosition(positions: List<Pair<Long, Int>>) {
        GlobalScope.launch { checkboxEntryDao.setPositions(positions) }
    }

    fun setNoteReminder(zonedDateTime: ZonedDateTime) {
        GlobalScope.launch {
            dao.setNoteReminder(noteId!!, zonedDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))

            val epoch = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC")).toEpochSecond() * 1000

            //TODO: time is correct but dosent work when phone is locked
            setAlarm(context, epoch,
                PendingIntent.getBroadcast(context, 0, Intent(context, ReminderBroadcastReciever::class.java).apply {
                    putExtra("NOTE_ID", noteId!!)
                    addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
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

    fun onCheckboxRestore() {
        GlobalScope.launch { dao.setDeletionTime(noteId!!, null) }
    }

    fun onCheckboxDeletePermanently() {
        GlobalScope.launch { dao.deletePermanently(noteId!!) }
    }

    fun addAudioClip(path: String) {
        GlobalScope.launch { audioDao.save(AudioEntry(0, noteId!!, path)) }
    }

    fun addVideo(path: String) {
        GlobalScope.launch { videoDao.save(VideoEntry(0, noteId!!, path)) }
    }
}