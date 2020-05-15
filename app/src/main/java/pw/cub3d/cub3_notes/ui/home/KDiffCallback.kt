package pw.cub3d.cub3_notes.ui.home

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class KDiffCallback<T: Any>: DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem === newItem
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}