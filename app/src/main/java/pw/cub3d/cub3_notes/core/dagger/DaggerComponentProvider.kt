package pw.cub3d.cub3_notes.core.dagger

import android.app.Activity
import androidx.fragment.app.Fragment

interface DaggerComponentProvider {
    val component: NotesComponent
}

val Activity.injector get() = (application as DaggerComponentProvider).component

val Fragment.injector get() = (requireActivity().application as DaggerComponentProvider).component