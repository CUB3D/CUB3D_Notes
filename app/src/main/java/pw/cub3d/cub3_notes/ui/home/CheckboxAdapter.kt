package pw.cub3d.cub3_notes.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.core.database.entity.CheckboxEntry
import pw.cub3d.cub3_notes.databinding.HomeCheckboxEntryBinding

class HomeCheckboxAdapter(
    ctx: Context
): RecyclerView.Adapter<HomeCheckboxViewHolder>() {
    private val inflater = LayoutInflater.from(ctx)
    private var checkboxes = emptyList<CheckboxEntry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HomeCheckboxViewHolder(
        HomeCheckboxEntryBinding.inflate(inflater, parent, false)
    )

    override fun getItemCount() = checkboxes.size

    override fun onBindViewHolder(holder: HomeCheckboxViewHolder, position: Int) {
        holder.bind(checkboxes[position])

        println("Binding $checkboxes")
    }

    fun updateData(entries: List<CheckboxEntry>) {
        this.checkboxes = entries
        notifyDataSetChanged()
    }
}


class HomeCheckboxViewHolder(private val view: HomeCheckboxEntryBinding): RecyclerView.ViewHolder(view.root) {
    fun bind(checkboxEntry: CheckboxEntry) {
        view.checkboxEntry = checkboxEntry
    }
}
