package pw.cub3d.cub3_notes.ui.newnote

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.entity.Colour
import pw.cub3d.cub3_notes.databinding.ColourEntryBinding

class ColoursAdapter(
    ctx: Context,
    private val colours: List<Colour>,
    private val callback: (Colour) -> Unit
): RecyclerView.Adapter<ColourViewHolder>() {
    private val layoutInflater = LayoutInflater.from(ctx)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ColourViewHolder(
        DataBindingUtil.inflate(layoutInflater, R.layout.colour_entry, parent, false),
        callback
    )

    override fun getItemCount() = colours.size

    override fun onBindViewHolder(holder: ColourViewHolder, position: Int) {
        holder.bind(colours[position])
    }
}

class ColourViewHolder(
    private val view: ColourEntryBinding,
    private val callback: (Colour)->Unit
): RecyclerView.ViewHolder(view.root) {
    fun bind(colour: Colour) {
        if(colour.id == -1L) {
            view.background = R.drawable.ic_plus
            view.colorEntryButton.setOnClickListener {
                println("Open color modify fragment")
            }
        } else {
            view.background = colour.getColourId()
            view.colorEntryButton.setOnClickListener {
                callback(colour)
            }
        }
    }
}
