package pw.cub3d.cub3_notes.core.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

inline fun <X> LiveData<List<X>>.filter(crossinline filter: (X) -> Boolean): LiveData<List<X>> = MediatorLiveData<List<X>>().apply {
    addSource(this) { input ->
        value = input.filter { filter(it) }
    }
}

fun <T> ignoreFirstAssignment(data: LiveData<T>): LiveData<T> = MediatorLiveData<T>().apply {
    var updates = 0
    addSource(data) {
        if (updates > 0) {
            value = it
        }
        updates += 1
    }
}

fun <T> distinctUntilLengthChanges(data: LiveData<List<T>>): LiveData<List<T>> = MediatorLiveData<List<T>>().apply {
    addSource(data) {
        if (value == null || value!!.size != it.size) {
            value = it
        }
    }
}

fun <T> distinctUntilChangedPred(data: LiveData<T>, predicate: (old: T, new: T) -> Boolean) = MediatorLiveData<T>().apply {
    addSource(data) {
        if (value != null && it != null) {
            if (!predicate(value!!, it)) {
                value = it
            }
        } else {
            value = it
        }
    }
}

fun <T> LiveData<T>.distinctUntilChangedBy(predicate: (old: T, new: T) -> Boolean) = distinctUntilChangedPred(this, predicate)

fun <T> LiveData<T>.ignoreFirstValue() = ignoreFirstAssignment(this)
