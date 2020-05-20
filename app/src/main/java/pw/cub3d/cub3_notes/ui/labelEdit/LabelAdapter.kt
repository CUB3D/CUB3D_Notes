package pw.cub3d.cub3_notes.ui.labelEdit

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.core.database.entity.Label
import pw.cub3d.cub3_notes.databinding.LabelEntryBinding
import pw.cub3d.cub3_notes.ui.home.BaseAdapter

class LabelAdapter(
    ctx: Context
) : BaseAdapter<Label, LabelViewHolder>(ctx) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LabelViewHolder(
        LabelEntryBinding.inflate(layoutInflater, parent, false)
    )

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class LabelViewHolder(
    private val view: LabelEntryBinding
) : RecyclerView.ViewHolder(view.root) {

    fun bind(label: Label) {
        view.model = label
    }
}
