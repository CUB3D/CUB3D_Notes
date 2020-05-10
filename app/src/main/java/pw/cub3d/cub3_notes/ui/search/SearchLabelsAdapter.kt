package pw.cub3d.cub3_notes.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.core.database.entity.Label
import pw.cub3d.cub3_notes.databinding.NoteLabelEntryBinding

class SearchLabelsAdapter(
    ctx: Context,
    private val labels: List<Label>,
    private val callback: (Label)->Unit
) : RecyclerView.Adapter<NoteLabelEntry>() {
    private val layoutInflater = LayoutInflater.from(ctx)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NoteLabelEntry(
        NoteLabelEntryBinding.inflate(layoutInflater, parent, false),
        callback
    )

    override fun getItemCount() = labels.size

    override fun onBindViewHolder(holder: NoteLabelEntry, position: Int) {
        holder.bind(labels[position])
    }
}

class NoteLabelEntry(
    private val noteLabelEntryBinding: NoteLabelEntryBinding,
    private val callback: (Label)->Unit
): RecyclerView.ViewHolder(noteLabelEntryBinding.root) {
    fun bind(label: Label) {
        noteLabelEntryBinding.label = label
        noteLabelEntryBinding.chip4.setOnClickListener {
            callback(label)
        }
    }
}
