package pw.cub3d.cub3_notes.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.intellij.lang.annotations.MagicConstant
import pw.cub3d.cub3_notes.ui.home.HomeFragment
import pw.cub3d.cub3_notes.ui.newnote.NewNoteFragment
import pw.cub3d.cub3_notes.ui.settings.SettingsFragment

@Module
abstract class ScreenBindingModule {
    @ContributesAndroidInjector
    abstract fun newNoteFragment(): NewNoteFragment
    @ContributesAndroidInjector
    abstract fun homeFragment(): HomeFragment
    @ContributesAndroidInjector
    abstract fun settingsFragment(): SettingsFragment
}