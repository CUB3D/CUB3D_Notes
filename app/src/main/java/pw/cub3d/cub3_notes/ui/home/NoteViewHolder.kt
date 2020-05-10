package pw.cub3d.cub3_notes.ui.home

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.noties.markwon.Markwon
import pw.cub3d.cub3_notes.core.database.entity.Note
import pw.cub3d.cub3_notes.core.database.entity.NoteAndCheckboxes
import pw.cub3d.cub3_notes.core.manager.AudioManager
import pw.cub3d.cub3_notes.core.manager.StorageManager
import pw.cub3d.cub3_notes.databinding.NoteEntryBinding

class NoteViewHolder(
    private val view: NoteEntryBinding,
    private val callback: (Note, NoteEntryBinding) -> Unit,
    var pos: Int,
    private val selectionTracker: SelectionTracker<Long>,
    private val markwon: Markwon
): RecyclerView.ViewHolder(view.root), ProvidesItemDetails {

    fun bind(note: NoteAndCheckboxes) {

        view.note = note.note

        markwon.setMarkdown(view.noteTitle, note.note.title)
        markwon.setMarkdown(view.noteText, note.note.text)

        val image = note.images.firstOrNull()

        if (image != null) {
            view.noteImage.visibility = View.VISIBLE

            Glide.with(view.root)
                .load(image.getFile(view.root.context))
                .into(view.noteImage)
        } else {
            view.noteImage.visibility = View.GONE
        }

        if(note.checkboxes.isNotEmpty()) {
            val unticked = note.checkboxes.filterNot { it.checked }.sortedByDescending { it.position }

            view.tickedItemCount = note.checkboxes.size - unticked.size
            view.untickedItemCount = unticked.size
            view.noteChecks.layoutManager = LinearLayoutManager(view.root.context)
            view.noteChecks.adapter = HomeCheckboxAdapter(view.root.context, unticked)
            view.noteChecks.visibility = View.VISIBLE
        } else {
            view.noteChecks.visibility = View.GONE
        }

        println("Drawing labels: $note")

        if(note.labels.isNotEmpty()) {
            view.noteLabels.layoutManager =
                LinearLayoutManager(view.root.context, LinearLayoutManager.HORIZONTAL, false)
            view.noteLabels.adapter = NoteLabelsAdapter(view.root.context, note.labels)
        } else {
            view.noteLabels.visibility = View.GONE
        }

        if(note.audioClips.isNotEmpty()) {
            view.notePlayAudio.setOnClickListener {
                AudioManager(StorageManager(view.root.context))
                    .playAudio(note.audioClips.first().fileName)
            }
            view.notePlayAudio.visibility = View.VISIBLE
        } else {
            view.notePlayAudio.visibility = View.GONE
        }

        if(note.videos.isNotEmpty()) {
            view.notePlayAudio.setOnClickListener {
                AudioManager(StorageManager(view.root.context))
                    .playAudio(note.videos.first().fileName)
            }
            view.notePlayAudio.visibility = View.VISIBLE
        } else {
            view.notePlayAudio.visibility = View.GONE
        }

        view.noteClickRoot.setOnClickListener { callback.invoke(note.note, view) }

        if(note.isEmpty()) {
            view.noteEmptyNotice.visibility = View.VISIBLE
        }
    }

    override fun getItemDetails(key: Long): ItemDetailsLookup.ItemDetails<Long> {
        println("Get item details for $this")
        return object: ItemDetailsLookup.ItemDetails<Long>() {
            override fun getSelectionKey() = key
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

    override fun getItemPosition() = pos

    fun onSelected() {
        view.isSelected = selectionTracker.isSelected(pos.toLong())
    }
}