package pw.cub3d.cub3_notes.core.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

inline fun <X> LiveData<List<X>>.filter(crossinline filter: (X) -> Boolean): LiveData<List<X>> = MediatorLiveData<List<X>>().apply {
    addSource(this) { input ->
        value = input.filter { filter(it) }
    }
}