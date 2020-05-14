package pw.cub3d.cub3_notes.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.core.database.entity.Label
import pw.cub3d.cub3_notes.databinding.NoteLabelEntryBinding

class NoteLabelsAdapter(
    ctx: Context
) : RecyclerView.Adapter<NoteLabelEntry>() {
    private val layoutInflater = LayoutInflater.from(ctx)
    private var labels = emptyList<Label>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NoteLabelEntry(
        NoteLabelEntryBinding.inflate(layoutInflater, parent, false)
    )

    override fun getItemCount() = labels.size

    override fun onBindViewHolder(holder: NoteLabelEntry, position: Int) {
        holder.bind(labels[position])
    }

    fun updateData(entries: List<Label>) {
        this.labels = entries
        notifyDataSetChanged()
    }
}

class NoteLabelEntry(
    private val noteLabelEntryBinding: NoteLabelEntryBinding
): RecyclerView.ViewHolder(noteLabelEntryBinding.root) {
    fun bind(label: Label) {
        noteLabelEntryBinding.label = label
    }
}
