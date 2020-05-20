package pw.cub3d.cub3_notes.core.dagger

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton
import pw.cub3d.cub3_notes.core.database.dao.NotesDao
import pw.cub3d.cub3_notes.ui.MainActivityViewModel
import pw.cub3d.cub3_notes.ui.archived.ArchiveViewModel
import pw.cub3d.cub3_notes.ui.colours.ColoursViewModel
import pw.cub3d.cub3_notes.ui.deleted.DeletedNotesViewModel
import pw.cub3d.cub3_notes.ui.home.HomeViewModel
import pw.cub3d.cub3_notes.ui.labelEdit.LabelEditViewModel
import pw.cub3d.cub3_notes.ui.newnote.NewNoteViewModel
import pw.cub3d.cub3_notes.ui.noteLabels.NoteLabelEditViewModel
import pw.cub3d.cub3_notes.ui.search.SearchViewModel
import pw.cub3d.cub3_notes.ui.settings.SettingsViewModel

@Singleton
@Component(modules = [ DatabaseModule::class ])
interface NotesComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(app: Context): Builder

        fun build(): NotesComponent
    }

    fun mainViewModelFactory(): ViewModelFactory<MainActivityViewModel>

    fun homeViewModelFactory(): ViewModelFactory<HomeViewModel>
    fun archiveViewModelFactory(): ViewModelFactory<ArchiveViewModel>
    fun newNoteViewModelFactory(): ViewModelFactory<NewNoteViewModel>
    fun deleteNoteViewModelFactory(): ViewModelFactory<DeletedNotesViewModel>
    fun searchViewModelFactory(): ViewModelFactory<SearchViewModel>
    fun noteLabelEditViewModelFactory(): ViewModelFactory<NoteLabelEditViewModel>
    fun labelEditViewModelFactory(): ViewModelFactory<LabelEditViewModel>
    fun coloursViewModelFactory(): ViewModelFactory<ColoursViewModel>
    fun settingsViewModelFactory(): ViewModelFactory<SettingsViewModel>

    fun notesDao(): NotesDao
}
