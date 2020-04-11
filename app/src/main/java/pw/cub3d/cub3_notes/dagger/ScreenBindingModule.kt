package pw.cub3d.cub3_notes.dagger

import androidx.room.Delete
import com.bumptech.glide.provider.ModelToResourceClassCache
import dagger.Module
import dagger.android.ContributesAndroidInjector
import pw.cub3d.cub3_notes.activity.MainActivity
import pw.cub3d.cub3_notes.ui.archived.ArchiveFragment
import pw.cub3d.cub3_notes.ui.colours.ColoursFragment
import pw.cub3d.cub3_notes.ui.deleted.DeletedNotesFragment
import pw.cub3d.cub3_notes.ui.home.HomeFragment
import pw.cub3d.cub3_notes.ui.labelEdit.LabelEditFragment
import pw.cub3d.cub3_notes.ui.newnote.NewNoteFragment
import pw.cub3d.cub3_notes.ui.noteLabels.NoteLabelEditFragment
import pw.cub3d.cub3_notes.ui.search.SearchFragment
import pw.cub3d.cub3_notes.ui.settings.SettingsFragment

@Module
abstract class ScreenBindingModule {
    @ContributesAndroidInjector
    abstract fun newNoteFragment(): NewNoteFragment
    @ContributesAndroidInjector
    abstract fun homeFragment(): HomeFragment
    @ContributesAndroidInjector
    abstract fun settingsFragment(): SettingsFragment
    @ContributesAndroidInjector
    abstract fun labelEditFragment(): LabelEditFragment
    @ContributesAndroidInjector
    abstract fun noteLabelEditFragment(): NoteLabelEditFragment
    @ContributesAndroidInjector
    abstract fun archiveFragment(): ArchiveFragment
    @ContributesAndroidInjector
    abstract fun searchFragment(): SearchFragment
    @ContributesAndroidInjector
    abstract fun coloursFragment(): ColoursFragment
    @ContributesAndroidInjector
    abstract fun deletedNotesFragment(): DeletedNotesFragment

    @ContributesAndroidInjector
    abstract fun mainActivity(): MainActivity
}