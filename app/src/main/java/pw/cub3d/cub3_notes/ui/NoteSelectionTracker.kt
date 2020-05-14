package pw.cub3d.cub3_notes.ui

import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.ui.home.MyItemKeyProvider
import pw.cub3d.cub3_notes.ui.home.NoteViewHolder

class NoteSelectionTracker(val view: RecyclerView, private val keyProvider: MyItemKeyProvider): SelectionTracker.SelectionObserver<Long>() {
    override fun onItemStateChanged(key: Long, selected: Boolean) {
        val pos = keyProvider.getPosition(key)
        view.findViewHolderForAdapterPosition(pos)?.let {
            (it as NoteViewHolder).onSelected()
        }
    }
}