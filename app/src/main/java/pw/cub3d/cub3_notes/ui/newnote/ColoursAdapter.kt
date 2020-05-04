package pw.cub3d.cub3_notes.ui.newnote

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.entity.Colour
import pw.cub3d.cub3_notes.databinding.ColourEntryBinding

class ColoursAdapter(
    private val frag: Fragment,
    private val colours: List<Colour>,
    private val viewModel: NewNoteViewModel
): RecyclerView.Adapter<ColourViewHolder>() {
    private val layoutInflater = LayoutInflater.from(frag.requireContext())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ColourViewHolder(
        DataBindingUtil.inflate(layoutInflater, R.layout.colour_entry, parent, false),
        viewModel,
        frag
    )

    override fun getItemCount() = colours.size

    override fun onBindViewHolder(holder: ColourViewHolder, position: Int) {
        holder.bind(colours[position])
    }
}

class ColourViewHolder(
    private val view: ColourEntryBinding,
    private val viewModel: NewNoteViewModel,
    private val frag: Fragment
): RecyclerView.ViewHolder(view.root) {
    fun bind(colour: Colour) {
        if(colour.id == -1L) {
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
