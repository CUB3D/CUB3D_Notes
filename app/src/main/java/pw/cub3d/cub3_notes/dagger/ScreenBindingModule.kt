package pw.cub3d.cub3_notes.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import pw.cub3d.cub3_notes.ui.newnote.NewNoteFragment

@Module
abstract class ScreenBindingModule {
    @ContributesAndroidInjector
    abstract fun newNoteFragment(): NewNoteFragment
}