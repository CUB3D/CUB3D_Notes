package pw.cub3d.cub3_notes.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import pw.cub3d.cub3_notes.core.manager.Layouts
import pw.cub3d.cub3_notes.core.manager.SettingsManager

class NoteLayoutManager (
    private val lifecycle: LifecycleOwner,
    private val settingsManager: SettingsManager
): StaggeredGridLayoutManager(Layouts.GRID.grid_size, StaggeredGridLayoutManager.VERTICAL) {
    init {
        settingsManager.noteLayout.observe(lifecycle, Observer {
            spanCount = it.grid_size
        })
    }
}