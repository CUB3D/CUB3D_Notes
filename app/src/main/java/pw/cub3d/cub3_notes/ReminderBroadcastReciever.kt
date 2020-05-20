package pw.cub3d.cub3_notes

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import pw.cub3d.cub3_notes.core.dagger.getInjector
import pw.cub3d.cub3_notes.core.database.dao.NotesDao
import pw.cub3d.cub3_notes.ui.MainActivity

class ReminderBroadcastReciever : BroadcastReceiver() {
    lateinit var notesDao: NotesDao

    override fun onReceive(context: Context, intent: Intent) {
        notesDao = getInjector(context).notesDao()

        println("Got broadcast")

        val noteId = intent.getLongExtra("NOTE_ID", -1).takeIf { it > 0 }
        noteId?.let { id ->
            notesDao.getNote(id)?.let { note ->
                println("Got reminder for note $note")

                val notification = NotificationCompat.Builder(context, "default").apply {
                    setSmallIcon(R.drawable.ic_bell)
                    setContentTitle(note.title)
                    setContentText(note.text)
                    priority = NotificationCompat.PRIORITY_DEFAULT
                    setContentIntent(PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java).apply {
                        putExtra("NOTE_ID", id)
                    }, 0))
                }.build()
                (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
                    notify(note.id.toInt(), notification)
                }
            } ?: println("Got reminder for invalid note id, deleted?")
        } ?: println("Got reminder without note id, Hmm")
    }
}
