package pw.cub3d.cub3_notes.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.core.database.entity.CheckboxEntry
import pw.cub3d.cub3_notes.databinding.HomeCheckboxEntryBinding

class HomeCheckboxAdapter(
    ctx: Context
): BaseAdapter<CheckboxEntry, HomeCheckboxViewHolder>(ctx) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HomeCheckboxViewHolder(
        HomeCheckboxEntryBinding.inflate(layoutInflater, parent, false)
    )

    override fun onBindViewHolder(holder: HomeCheckboxViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}


class HomeCheckboxViewHolder(private val view: HomeCheckboxEntryBinding): RecyclerView.ViewHolder(view.root) {
    fun bind(checkboxEntry: CheckboxEntry) {
        view.checkboxEntry = checkboxEntry
    }
}
