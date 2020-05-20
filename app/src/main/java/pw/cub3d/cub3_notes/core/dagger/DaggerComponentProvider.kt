package pw.cub3d.cub3_notes.core.dagger

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import androidx.fragment.app.Fragment

interface DaggerComponentProvider {
    val component: NotesComponent
}

val Activity.injector get() = (application as DaggerComponentProvider).component

val Fragment.injector get() = (requireActivity().application as DaggerComponentProvider).component

fun BroadcastReceiver.getInjector(ctx: Context) = (ctx.applicationContext as DaggerComponentProvider).component
