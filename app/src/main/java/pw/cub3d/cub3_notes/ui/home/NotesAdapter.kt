package pw.cub3d.cub3_notes.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.entity.Note
import pw.cub3d.cub3_notes.databinding.NoteEntryBinding

class NotesAdapter(
    ctx: Context,
    private val notes: List<Note>,
    private val callback: (Note)->Unit
) : RecyclerView.Adapter<NoteViewHolder>() {
    lateinit var selectionTracker: SelectionTracker<Long>

    private val inflater = LayoutInflater.from(ctx)

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NoteViewHolder(
        DataBindingUtil.inflate(inflater, R.layout.note_entry, parent, false),
        callback,
        0,
        selectionTracker
    )

    override fun getItemCount() = notes.size

    override fun getItemId(position: Int) = position.toLong()

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.pos = position
        println("Bind view: $position, selected: ${selectionTracker.isSelected(position.toLong())}")
        holder.bind(notes[position])
    }

}

