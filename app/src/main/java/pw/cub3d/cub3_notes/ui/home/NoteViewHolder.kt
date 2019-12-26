package pw.cub3d.cub3_notes.ui.home

import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.entity.Note

class NoteViewHolder(
    private val view: View,
    private val callback: (Note) -> Unit,
    var pos: Int,
    val selectionTracker: SelectionTracker<Long>
): RecyclerView.ViewHolder(view) {
    private val title = view.findViewById<TextView>(
        R.id.note_title
    )!!
    private val text = view.findViewById<TextView>(
        R.id.note_text
    )!!

    private val root = view.findViewById<CardView>(
        R.id.note_root
    )!!

    private val checkboxes = view.findViewById<RecyclerView>(R.id.note_checks)!!

    fun bind(note: Note) {
        if(note.title.isNotEmpty()) {
            title.text = note.title
            title.visibility = View.VISIBLE
        } else {
            title.visibility = View.GONE
        }

        if(note.text.isNotEmpty()) {
            text.text = note.text
            text.visibility = View.VISIBLE
        } else {
            text.visibility = View.GONE
        }

        if(note.type == Note.TYPE_CHECKBOX) {
            println("Drawing checkbox note $note")
            checkboxes.layoutManager = LinearLayoutManager(view.context)
            checkboxes.adapter = HomeCheckboxAdapter(view.context, note.checkboxEntry)
            checkboxes.visibility = View.VISIBLE
        } else {
            checkboxes.visibility = View.GONE
        }


        root.setOnClickListener { callback.invoke(note) }
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
        if(selectionTracker.isSelected(pos.toLong())) {
            root.setBackgroundColor(Color.BLUE)
        } else {
            root.setBackgroundColor(Color.WHITE)
        }
    }
}