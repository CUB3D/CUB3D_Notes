package pw.cub3d.cub3_notes.ui.home

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.core.database.entity.Label
import pw.cub3d.cub3_notes.databinding.NoteLabelEntryBinding

class NoteLabelsAdapter(
    ctx: Context
) : BaseAdapter<Label, NoteLabelEntry>(ctx) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NoteLabelEntry(
        NoteLabelEntryBinding.inflate(layoutInflater, parent, false)
    )

    override fun onBindViewHolder(holder: NoteLabelEntry, position: Int) {
        holder.bind(getItem(position))
    }
}

class NoteLabelEntry(
    private val noteLabelEntryBinding: NoteLabelEntryBinding
) : RecyclerView.ViewHolder(noteLabelEntryBinding.root) {
    fun bind(label: Label) {
        noteLabelEntryBinding.label = label
    }
}
