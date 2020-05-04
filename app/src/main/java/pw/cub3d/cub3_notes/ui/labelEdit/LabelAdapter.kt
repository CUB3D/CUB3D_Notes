package pw.cub3d.cub3_notes.ui.labelEdit

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.entity.Label
import pw.cub3d.cub3_notes.databinding.LabelEntryBinding

class LabelAdapter(
    ctx: Context,
    private val labels: List<Label>
): RecyclerView.Adapter<LabelViewHolder>() {
    private val inflater = LayoutInflater.from(ctx)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = LabelViewHolder(
        LabelEntryBinding.inflate(inflater, parent, false)
    )

    override fun getItemCount() = labels.size

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        holder.bind(labels[position])
    }
}

class LabelViewHolder(
    private val view: LabelEntryBinding
): RecyclerView.ViewHolder(view.root) {

    fun bind(label: Label) {
        view.model = label
    }
}
