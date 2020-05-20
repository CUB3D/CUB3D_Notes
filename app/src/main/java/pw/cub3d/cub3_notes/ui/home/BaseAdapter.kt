package pw.cub3d.cub3_notes.ui.home

import android.content.Context
import android.view.LayoutInflater
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T: Any, VH: RecyclerView.ViewHolder>(
    private val ctx: Context
): ListAdapter<T, VH>(KDiffCallback<T>()) {
    val layoutInflater = LayoutInflater.from(ctx)!!

    val items: List<T>
        get() = currentList

    init {
        setHasStableIds(true)
    }

    fun bindToLiveData(lifecycle: LifecycleOwner, data: LiveData<List<T>>) {
        data.observe(lifecycle, Observer { submitList(it) })
    }
}