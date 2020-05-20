package pw.cub3d.cub3_notes.ui.newnote

import android.graphics.Color
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.core.database.entity.Colour
import pw.cub3d.cub3_notes.databinding.ColourEntryBinding
import pw.cub3d.cub3_notes.ui.home.BaseAdapter

class ColoursAdapter(
    private val frag: Fragment,
    private val viewModel: NewNoteViewModel
) : BaseAdapter<Colour, ColourViewHolder>(frag.requireContext()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ColourViewHolder(
        ColourEntryBinding.inflate(layoutInflater, parent, false),
        viewModel,
        frag
    )

    override fun onBindViewHolder(holder: ColourViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ColourViewHolder(
    private val view: ColourEntryBinding,
    private val viewModel: NewNoteViewModel,
    private val frag: Fragment
) : RecyclerView.ViewHolder(view.root) {
    fun bind(colour: Colour) {
        if (colour.id == -1L) {
            view.background = Color.WHITE
            view.colourEntryButton.icon = frag.resources.getDrawable(R.drawable.ic_plus)
            view.colourEntryButton.setOnClickListener {
                frag.findNavController().navigate(R.id.nav_colours)
            }
        } else {
            view.colourEntryButton.icon = null
            view.background = colour.getColourId()
            view.colourEntryButton.setOnClickListener {
                viewModel.setNoteColour(colour.hex_colour)
            }
        }
    }
}
