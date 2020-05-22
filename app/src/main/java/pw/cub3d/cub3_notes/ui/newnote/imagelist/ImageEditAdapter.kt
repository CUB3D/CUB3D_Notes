package pw.cub3d.cub3_notes.ui.newnote.imagelist

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.core.database.entity.ImageEntry
import pw.cub3d.cub3_notes.core.utils.GlideApp
import pw.cub3d.cub3_notes.databinding.ImageEditEntryBinding
import pw.cub3d.cub3_notes.databinding.ImageEntryBinding
import pw.cub3d.cub3_notes.ui.home.BaseAdapter
import pw.cub3d.cub3_notes.ui.newnote.NewNoteViewModel

class ImageEditAdapter(
    private val ctx: Context,
    private val callbacks: ImageEditViewHolderCallbacks
) : BaseAdapter<ImageEntry, ImageViewHolder>(ctx) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ImageViewHolder(
        ImageEditEntryBinding.inflate(layoutInflater, parent, false),
        callbacks
    )

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ImageViewHolder(
    val view: ImageEditEntryBinding,
    val callbacks: ImageEditViewHolderCallbacks
) : RecyclerView.ViewHolder(view.root) {
    fun bind(item: ImageEntry) {
        GlideApp.with(view.root)
            .load(item.getFile(view.root.context))
            .into(view.imageEntryImage)

        view.imageEntryDelete.setOnClickListener {
            callbacks.onDelete(item)
        }
    }
}

class ImageEditViewHolderCallbacks(val vm: NewNoteViewModel) {
    fun onDelete(item: ImageEntry) {
        vm.onImageDelete(item)
    }
}
