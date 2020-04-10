package pw.cub3d.cub3_notes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderBroadcastReciever: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        println("Got broadcast")
    }
}