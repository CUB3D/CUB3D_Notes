package pw.cub3d.cub3_notes.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.entity.Label
import pw.cub3d.cub3_notes.databinding.NoteLabelEntryBinding

class NoteLabelsAdapter(
    ctx: Context,
    private val labels: List<Label>
) : RecyclerView.Adapter<NoteLabelEntry>() {
    private val layoutInflater = LayoutInflater.from(ctx)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NoteLabelEntry(
        NoteLabelEntryBinding.inflate(layoutInflater, parent, false)
    )

    override fun getItemCount() = labels.size

    override fun onBindViewHolder(holder: NoteLabelEntry, position: Int) {
        println("Binding label: ${labels[position]}")
        holder.bind(labels[position])
    }
}

class NoteLabelEntry(
    private val noteLabelEntryBinding: NoteLabelEntryBinding
): RecyclerView.ViewHolder(noteLabelEntryBinding.root) {
    fun bind(label: Label) {
        noteLabelEntryBinding.label = label
    }
}
