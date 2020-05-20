package pw.cub3d.cub3_notes

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import pw.cub3d.cub3_notes.core.dagger.DaggerComponentProvider
import pw.cub3d.cub3_notes.core.dagger.DaggerNotesComponent
import pw.cub3d.cub3_notes.core.dagger.NotesComponent

class NoteApplication : Application(), DaggerComponentProvider {

    override fun onCreate() {
        super.onCreate()

        comp = DaggerNotesComponent.builder()
            .context(this)
            .build()

        AndroidThreeTen.init(this)
    }

    private lateinit var comp: NotesComponent
    override val component: NotesComponent
        get() = comp
}
