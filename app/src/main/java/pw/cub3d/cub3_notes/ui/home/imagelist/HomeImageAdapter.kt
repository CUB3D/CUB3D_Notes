package pw.cub3d.cub3_notes.ui.home.imagelist

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.core.database.entity.ImageEntry
import pw.cub3d.cub3_notes.core.utils.GlideApp
import pw.cub3d.cub3_notes.databinding.ImageEntryBinding
import pw.cub3d.cub3_notes.ui.home.BaseAdapter

class HomeImageAdapter(private val ctx: Context) : BaseAdapter<ImageEntry, HomeImageViewHolder>(ctx) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HomeImageViewHolder(
        ImageEntryBinding.inflate(layoutInflater, parent, false)
    )

    override fun onBindViewHolder(holder: HomeImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class HomeImageViewHolder(val view: ImageEntryBinding) : RecyclerView.ViewHolder(view.root) {
    fun bind(item: ImageEntry) {
        GlideApp.with(view.root)
            .load(item.getFile(view.root.context))
            .into(view.imageEntryImage)
    }
}
