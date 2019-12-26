package pw.cub3d.cub3_notes.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.entity.CheckboxEntry
import pw.cub3d.cub3_notes.databinding.HomeCheckboxEntryBinding

class HomeCheckboxAdapter(
    ctx: Context,
    private val checkboxes: List<CheckboxEntry>
): RecyclerView.Adapter<HomeCheckboxViewHolder>() {
    private val inflater = LayoutInflater.from(ctx)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HomeCheckboxViewHolder(
        DataBindingUtil.inflate(inflater, R.layout.home_checkbox_entry, parent, false)
    )

    override fun getItemCount() = checkboxes.size

    override fun onBindViewHolder(holder: HomeCheckboxViewHolder, position: Int) {
        holder.bind(checkboxes[position])
    }
}


class HomeCheckboxViewHolder(private val view: HomeCheckboxEntryBinding): RecyclerView.ViewHolder(view.root) {
    fun bind(checkboxEntry: CheckboxEntry) {
        view.checkboxEntry = checkboxEntry
    }
}
