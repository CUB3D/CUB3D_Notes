package pw.cub3d.cub3_notes.ui

import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.ui.home.ItemDetailsProvider
import pw.cub3d.cub3_notes.ui.home.NotesAdapter

object NoteSelectionTrackerFactory {
    fun buildTracker(tag: String, view: RecyclerView): SelectionTracker<Long> {
        val itemDetailsProvider = ItemDetailsProvider(view)

        val tracker = SelectionTracker.Builder(
            tag,
            view,
            itemDetailsProvider.keyProvider,
            itemDetailsProvider,
            StorageStrategy.createLongStorage()
        )
            .withSelectionPredicate(SelectionPredicates.createSelectAnything())
            .build()

        tracker.addObserver(NoteSelectionTracker(view, itemDetailsProvider.keyProvider))

        return tracker
    }
}

fun SelectionTracker<Long>.bind(notesAdapter: NotesAdapter) {
    notesAdapter.selectionTracker = this
}