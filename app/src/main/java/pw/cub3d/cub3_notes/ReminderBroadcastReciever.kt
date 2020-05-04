package pw.cub3d.cub3_notes

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import pw.cub3d.cub3_notes.activity.MainActivity
import pw.cub3d.cub3_notes.database.dao.NotesDao
import javax.inject.Inject

class ReminderBroadcastReciever: BroadcastReceiver() {
    @Inject lateinit var notesDao: NotesDao

    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)

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