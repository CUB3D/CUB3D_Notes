package pw.cub3d.cub3_notes

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.sentry.Sentry
import io.sentry.android.AndroidSentryClientFactory
import pw.cub3d.cub3_notes.core.dagger.DaggerComponentProvider
import pw.cub3d.cub3_notes.core.dagger.DaggerNotesComponent
import pw.cub3d.cub3_notes.core.dagger.NotesComponent
import javax.inject.Inject

class NoteApplication: Application(), HasAndroidInjector, DaggerComponentProvider {

    override fun onCreate() {
        super.onCreate()

        DaggerNotesComponent.builder()
            .context(this)
            .build()
            .apply {
                comp = this
            }
            .inject(this)

        AndroidThreeTen.init(this)

        Sentry.init("https://a53e05e0eccb4bac9c4dd5aae56bcbc3@sentry.io/1869522", AndroidSentryClientFactory(this))

    }

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>
    override fun androidInjector() = dispatchingAndroidInjector

    lateinit var comp: NotesComponent
    override val component: NotesComponent
        get() = comp
}