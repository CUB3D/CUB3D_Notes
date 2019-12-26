package pw.cub3d.cub3_notes.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.entity.CheckboxEntry

class HomeCheckboxAdapter(
    ctx: Context,
    private val checkboxes: List<CheckboxEntry>
): RecyclerView.Adapter<HomeCheckboxViewHolder>() {
    private val inflater = LayoutInflater.from(ctx)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HomeCheckboxViewHolder(
        inflater.inflate(R.layout.home_checkbox_entry, parent, false)
    )

    override fun getItemCount() = checkboxes.size

    override fun onBindViewHolder(holder: HomeCheckboxViewHolder, position: Int) {
        holder.bind(checkboxes[position])
    }
}

class HomeCheckboxViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val checkbox = view.findViewById<CheckBox>(R.id.homeCheckboxEntry_checkbox)!!

    fun bind(checkboxEntry: CheckboxEntry) {
        checkbox.isChecked = false
        checkbox.text = checkboxEntry.content
    }
}
