package pw.cub3d.cub3_notes.dagger

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import pw.cub3d.cub3_notes.NoteApplication
import pw.cub3d.cub3_notes.ui.archived.ArchiveViewModel
import pw.cub3d.cub3_notes.ui.home.HomeViewModel
import pw.cub3d.cub3_notes.ui.newnote.NewNoteViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [ AndroidInjectionModule::class, DatabaseModule::class, ScreenBindingModule::class ])
interface NotesComponent: AndroidInjector<NoteApplication> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(app: Context): Builder

        fun build(): NotesComponent
    }

    fun homeViewModelFactory(): ViewModelFactory<HomeViewModel>
    fun archiveViewModelFactory(): ViewModelFactory<ArchiveViewModel>
    fun newNoteViewModelFactory(): ViewModelFactory<NewNoteViewModel>
}