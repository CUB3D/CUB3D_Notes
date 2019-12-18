package pw.cub3d.cub3_notes

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import pw.cub3d.cub3_notes.dagger.DaggerNotesComponent
import javax.inject.Inject

class NoteApplication: Application(), HasAndroidInjector {

    override fun onCreate() {
        super.onCreate()

        DaggerNotesComponent.builder()
            .context(this)
            .build()
            .inject(this)

        AndroidThreeTen.init(this)
    }

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>
    override fun androidInjector() = dispatchingAndroidInjector
}