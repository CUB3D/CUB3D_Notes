package pw.cub3d.cub3_notes.ui.newnote

import android.R.attr.path
import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.StorageManager
import pw.cub3d.cub3_notes.database.entity.AudioEntry
import pw.cub3d.cub3_notes.databinding.AudioEntryBinding
import java.io.File


class AudioAdapter(
    private val ctx: Context
): RecyclerView.Adapter<AudioEntryViewHolder>() {
    var entries = emptyList<AudioEntry>()

    private val layoutInflater = LayoutInflater.from(ctx)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AudioEntryViewHolder(
        AudioEntryBinding.inflate(layoutInflater, parent, false)
    )

    override fun onBindViewHolder(holder: AudioEntryViewHolder, position: Int) {
        holder.bind(entries[position])
    }

    override fun getItemCount() = entries.size

    fun updateData(entries: List<AudioEntry>) {
        this.entries = entries
        notifyDataSetChanged()
    }
}

class AudioEntryViewHolder(val binding: AudioEntryBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(audioEntry: AudioEntry) {
        binding.imageButton2.setOnClickListener {
            val mp = MediaPlayer()

            try {
                mp.setDataSource(File(StorageManager(binding.root.context).getAudioDir(), audioEntry.fileName).path)
                mp.prepare()
                mp.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}