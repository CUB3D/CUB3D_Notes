package pw.cub3d.cub3_notes.ui.settings

import androidx.lifecycle.ViewModel
import pw.cub3d.cub3_notes.core.database.DataExporter
import pw.cub3d.cub3_notes.core.manager.SettingsManager
import pw.cub3d.cub3_notes.core.sync.OpenTasksSyncManager
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    val dataExporter: DataExporter,
    val openTasksSyncManager: OpenTasksSyncManager,
    val settingsManager: SettingsManager
): ViewModel()