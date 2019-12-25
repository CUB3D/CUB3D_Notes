package pw.cub3d.cub3_notes.ui.newnote

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.entity.CheckboxEntry

class CheckBoxAdapter(
    ctx: Context,
    private val checkboxEntries: List<CheckboxEntry>
) : RecyclerView.Adapter<CheckBoxViewHolder>() {
    private val layoutInflater = LayoutInflater.from(ctx)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CheckBoxViewHolder(
        layoutInflater.inflate(R.layout.checkbox_entry, parent, false)
    )

    override fun getItemCount() = checkboxEntries.size

    override fun onBindViewHolder(holder: CheckBoxViewHolder, position: Int) {
        holder.bind(checkboxEntries[position])
    }
}

class CheckBoxViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val check = view.findViewById<CheckBox>(R.id.checkboxEntry_check)!!
    private val delete = view.findViewById<ImageView>(R.id.checkboxEntry_delete)!!

    fun bind(checkboxEntry: CheckboxEntry) {
        check.isChecked = checkboxEntry.checked
        delete.setOnClickListener {  println("Delete: ${checkboxEntry.id}") }
    }
}
