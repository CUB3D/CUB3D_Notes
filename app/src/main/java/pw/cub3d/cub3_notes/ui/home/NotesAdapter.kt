package pw.cub3d.cub3_notes.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.core.database.entity.Note
import pw.cub3d.cub3_notes.core.database.entity.NoteAndCheckboxes
import pw.cub3d.cub3_notes.databinding.NoteEntryBinding

class NotesAdapter(
    ctx: Context,
    public var notes: List<NoteAndCheckboxes>,
    private val callback: (Note, NoteEntryBinding)->Unit
) : RecyclerView.Adapter<NoteViewHolder>() {
    lateinit var selectionTracker: SelectionTracker<Long>

    private val inflater = LayoutInflater.from(ctx)

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = NoteViewHolder(
        NoteEntryBinding.inflate(inflater, parent, false),
        callback,
        0,
        selectionTracker
    )

    override fun getItemCount() = notes.size

    override fun getItemId(position: Int) = notes[position].note.id

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.pos = position
//        println("Bind view: $position, selected: ${selectionTracker.isSelected(position.toLong())}")
        holder.bind(notes[position])
    }

    fun updateData(it: List<NoteAndCheckboxes>) {
        notes = it
        notifyDataSetChanged()
    }

}

