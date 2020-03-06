package pw.cub3d.cub3_notes.ui.home

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.database.entity.Note
import pw.cub3d.cub3_notes.database.entity.NoteAndCheckboxes
import pw.cub3d.cub3_notes.databinding.NoteEntryBinding

class NoteViewHolder(
    private val view: NoteEntryBinding,
    private val callback: (Note) -> Unit,
    var pos: Int,
    private val selectionTracker: SelectionTracker<Long>
): RecyclerView.ViewHolder(view.root) {

    fun bind(note: NoteAndCheckboxes) {

        view.note = note.note

        if(note.note.type == Note.TYPE_CHECKBOX) {
            println("Drawing checkbox note $note")

            val unticked = note.checkboxes.filterNot { it.checked }

            view.tickedItemCount = note.checkboxes.size - unticked.size
            view.noteChecks.layoutManager = LinearLayoutManager(view.root.context)
            view.noteChecks.adapter = HomeCheckboxAdapter(view.root.context, unticked)
        }

        println("Drawing labels: $note")

        view.noteLabels.layoutManager = LinearLayoutManager(view.root.context, LinearLayoutManager.HORIZONTAL, false)
        view.noteLabels.adapter = NoteLabelsAdapter(view.root.context, note.labels)

        view.root.setOnClickListener { callback.invoke(note.note) }
    }

    fun getItemDetails(idd: Long?): ItemDetailsLookup.ItemDetails<Long>? {
        println("Get item details for $this")
        return object: ItemDetailsLookup.ItemDetails<Long>() {
            override fun getSelectionKey() = idd
            override fun getPosition() = pos
            override fun inDragRegion(e: MotionEvent): Boolean {
                return true
            }

            override fun inSelectionHotspot(e: MotionEvent): Boolean {
                return false
            }

            override fun hasSelectionKey(): Boolean {
                return true
            }
        }
    }

    fun onSelected() {
        view.isSelected = selectionTracker.isSelected(pos.toLong())
    }
}