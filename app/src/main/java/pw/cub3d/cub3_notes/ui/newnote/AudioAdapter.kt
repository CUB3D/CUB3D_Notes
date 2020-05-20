package pw.cub3d.cub3_notes.ui.newnote

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.core.database.entity.AudioEntry
import pw.cub3d.cub3_notes.core.manager.AudioManager
import pw.cub3d.cub3_notes.core.manager.StorageManager
import pw.cub3d.cub3_notes.databinding.AudioEntryBinding
import pw.cub3d.cub3_notes.ui.home.BaseAdapter

class AudioAdapter(
    ctx: Context
) : BaseAdapter<AudioEntry, AudioEntryViewHolder>(ctx) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AudioEntryViewHolder(
        AudioEntryBinding.inflate(layoutInflater, parent, false)
    )

    override fun onBindViewHolder(holder: AudioEntryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class AudioEntryViewHolder(val binding: AudioEntryBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(audioEntry: AudioEntry) {
        binding.imageButton2.setOnClickListener {
            AudioManager(StorageManager(binding.root.context))
                .playAudio(audioEntry.fileName)
        }
    }
}
