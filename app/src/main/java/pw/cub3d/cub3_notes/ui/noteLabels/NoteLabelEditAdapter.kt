package pw.cub3d.cub3_notes.ui.noteLabels

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pw.cub3d.cub3_notes.core.database.dao.LabelDao
import pw.cub3d.cub3_notes.core.database.entity.Label
import pw.cub3d.cub3_notes.core.database.entity.NoteLabel
import pw.cub3d.cub3_notes.databinding.NoteLabelEditEntryBinding

class NoteLabelEditAdapter(
    ctx: Context,
    private val labels: List<Pair<Label, Boolean>>,
    private val dao: LabelDao,
    private val noteId: Long
): RecyclerView.Adapter<NoteLabelEditViewHolder>() {

    private val layoutInflater = LayoutInflater.from(ctx)

    override fun getItemCount() = labels.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NoteLabelEditViewHolder(
        NoteLabelEditEntryBinding.inflate(layoutInflater, parent, false),
        dao,
        noteId
    )

    override fun onBindViewHolder(holder: NoteLabelEditViewHolder, position: Int) {
        holder.bind(labels[position])
    }
}

class NoteLabelEditViewHolder(
    private val view: NoteLabelEditEntryBinding,
    private val dao: LabelDao,
    private val noteId: Long
): RecyclerView.ViewHolder(view.root) {
    fun bind(label: Pair<Label, Boolean>) {
        view.model = label.first

        view.checked = MutableLiveData(label.second).apply {
            this.observeForever { isChecked ->
                if (isChecked != label.second) {
                    if (isChecked) {
                        println("Adding label: $label")
                        GlobalScope.launch {
                            dao.insert(NoteLabel(0, noteId, label.first.id))
                        }
                    } else {
                        println("Removing label: $label")
                        GlobalScope.launch {
                            dao.deleteNoteLabel(noteId, label.first.id)
                        }
                    }
                }
            }
        }
    }
}
