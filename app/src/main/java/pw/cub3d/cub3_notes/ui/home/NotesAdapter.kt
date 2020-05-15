package pw.cub3d.cub3_notes.ui.home

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.selection.SelectionTracker
import io.noties.markwon.Markwon
import pw.cub3d.cub3_notes.core.database.entity.Note
import pw.cub3d.cub3_notes.core.database.entity.NoteAndCheckboxes
import pw.cub3d.cub3_notes.databinding.NoteEntryBinding


class NotesAdapter(
    ctx: Context,
    private val callback: (Note, NoteEntryBinding) -> Unit
) : BaseAdapter<NoteAndCheckboxes, NoteViewHolder>(ctx) {

    val markwon = Markwon.builder(ctx).build()

    lateinit var selectionTracker: SelectionTracker<Long>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NoteViewHolder(
        NoteEntryBinding.inflate(layoutInflater, parent, false),
        callback,
        0,
        selectionTracker,
        markwon
    )

    override fun getItemId(position: Int) = getItem(position).note.id

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.pos = position
        holder.bind(getItem(position))
    }
}

