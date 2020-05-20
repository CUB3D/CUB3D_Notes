package pw.cub3d.cub3_notes.ui.home

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

class ItemDetailsProvider(private val recycler: RecyclerView) : ItemDetailsLookup<Long>() {
    val keyProvider = MyItemKeyProvider(recycler)

    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        recycler.findChildViewUnder(e.x, e.y)?.let { v ->
            val holder: RecyclerView.ViewHolder = recycler.getChildViewHolder(v)
            if (holder is ProvidesItemDetails) {
                return holder.getItemDetails(keyProvider.getKey(holder.getItemPosition())!!)
            }
        }
        return null
    }
}
