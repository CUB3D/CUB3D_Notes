package pw.cub3d.cub3_notes.ui.colours

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.core.database.entity.Colour
import pw.cub3d.cub3_notes.databinding.ColourEditEntryBinding

class ColoursAdapter(
    ctx: Context
) : RecyclerView.Adapter<ColourViewHolder>() {
    var colours = emptyList<Colour>()

    private val layoutInflater = LayoutInflater.from(ctx)

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ColourViewHolder(
        ColourEditEntryBinding.inflate(layoutInflater, parent, false)
    )

    override fun getItemCount() = colours.size

    override fun getItemId(position: Int) = colours[position].id

    override fun onBindViewHolder(holder: ColourViewHolder, position: Int) {
        holder.bind(colours[position])
    }

    fun updateData(entry: List<Colour>) {
        this.colours = entry
        notifyDataSetChanged()
    }
}

class ColourViewHolder(
    private val binding: ColourEditEntryBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(colour: Colour) {
        binding.colour = colour
    }
}
