package pw.cub3d.cub3_notes.ui.home

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import io.noties.markwon.Markwon
import pw.cub3d.cub3_notes.core.database.entity.Note
import pw.cub3d.cub3_notes.core.database.entity.NoteAndCheckboxes
import pw.cub3d.cub3_notes.core.manager.AudioManager
import pw.cub3d.cub3_notes.core.manager.StorageManager
import pw.cub3d.cub3_notes.core.utils.GlideApp
import pw.cub3d.cub3_notes.databinding.NoteEntryBinding
import pw.cub3d.cub3_notes.ui.home.imagelist.HomeImageAdapter

class NoteViewHolder(
    private val view: NoteEntryBinding,
    private val callback: (Note, NoteEntryBinding) -> Unit,
    var pos: Int,
    private val selectionTracker: SelectionTracker<Long>,
    private val markwon: Markwon
) : RecyclerView.ViewHolder(view.root), ProvidesItemDetails {

    private val checkboxAdapter = HomeCheckboxAdapter(view.root.context)
    private val labelAdapter = NoteLabelsAdapter(view.root.context)
    private val imageAdapter = HomeImageAdapter(view.root.context)

    init {
        view.noteChecks.layoutManager = LinearLayoutManager(view.root.context)
        view.noteChecks.adapter = checkboxAdapter

        view.noteLabels.layoutManager = LinearLayoutManager(view.root.context, LinearLayoutManager.HORIZONTAL, false)
        view.noteLabels.adapter = labelAdapter

        view.noteImage.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        view.noteImage.adapter = imageAdapter
    }

    fun bind(note: NoteAndCheckboxes) {
        view.note = note.note

        markwon.setMarkdown(view.noteTitle, note.note.title)
        markwon.setMarkdown(view.noteText, note.note.text)

        imageAdapter.submitList(note.images)
        view.noteImage.visibility = if(note.images.isEmpty()) View.GONE else View.VISIBLE

        if (note.checkboxes.isNotEmpty()) {
            val unticked = note.checkboxes.filterNot { it.checked }.sortedBy { it.position }

            view.tickedItemCount = note.checkboxes.size - unticked.size
            view.untickedItemCount = unticked.size
            checkboxAdapter.submitList(unticked)
            view.noteChecks.visibility = View.VISIBLE
        } else {
            view.noteChecks.visibility = View.GONE
        }

        if (note.labels.isNotEmpty()) {
            labelAdapter.submitList(note.labels)
            view.noteLabels.visibility = View.VISIBLE
        } else {
            view.noteLabels.visibility = View.GONE
        }

        if (note.audioClips.isNotEmpty()) {
            view.notePlayAudio.setOnClickListener {
                AudioManager(StorageManager(view.root.context))
                    .playAudio(note.audioClips.first().fileName)
            }
            view.notePlayAudio.visibility = View.VISIBLE
        } else {
            view.notePlayAudio.visibility = View.GONE
        }

        if (note.videos.isNotEmpty()) {
            view.notePlayAudio.setOnClickListener {
                AudioManager(StorageManager(view.root.context))
                    .playAudio(note.videos.first().fileName)
            }
            view.notePlayAudio.visibility = View.VISIBLE
        } else {
            view.notePlayAudio.visibility = View.GONE
        }

        view.noteClickRoot.setOnClickListener { callback.invoke(note.note, view) }

        if (note.isEmpty()) {
            view.noteEmptyNotice.visibility = View.VISIBLE
        }
    }

    override fun getItemDetails(key: Long): ItemDetailsLookup.ItemDetails<Long> {
        return object : ItemDetailsLookup.ItemDetails<Long>() {
            override fun getSelectionKey() = key
            override fun getPosition() = pos
            // TODO: support for drag handles
            override fun inDragRegion(e: MotionEvent): Boolean {
                return true
            }

            override fun inSelectionHotspot(e: MotionEvent) = false
            override fun hasSelectionKey() = true
        }
    }

    override fun getItemPosition() = pos

    fun onSelected() {
        view.isSelected = selectionTracker.isSelected(pos.toLong())
    }
}
