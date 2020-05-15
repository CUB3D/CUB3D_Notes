package pw.cub3d.cub3_notes.ui.colours

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.core.database.entity.Colour
import pw.cub3d.cub3_notes.databinding.ColourEditEntryBinding
import pw.cub3d.cub3_notes.ui.home.BaseAdapter

class ColoursAdapter(
    ctx: Context
) : BaseAdapter<Colour, ColourViewHolder>(ctx) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ColourViewHolder(
        ColourEditEntryBinding.inflate(layoutInflater, parent, false)
    )

    override fun getItemId(position: Int) = getItem(position).id

    override fun onBindViewHolder(holder: ColourViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ColourViewHolder(
    private val binding: ColourEditEntryBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(colour: Colour) {
        binding.colour = colour
    }
}
