package pw.cub3d.cub3_notes.dagger

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import pw.cub3d.cub3_notes.NoteApplication
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
}