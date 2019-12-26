package pw.cub3d.cub3_notes.ui.newnote

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.entity.CheckboxEntry

class CheckBoxAdapter(
    ctx: Context,
    private val checkboxEntries: List<CheckboxEntry>,
    private val checkedCallback: (CheckboxEntry)->Unit,
    private val deleteCallback: (CheckboxEntry)->Unit
) : RecyclerView.Adapter<CheckBoxViewHolder>() {
    private val layoutInflater = LayoutInflater.from(ctx)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CheckBoxViewHolder(
        layoutInflater.inflate(R.layout.checkbox_entry, parent, false),
        checkedCallback,
        deleteCallback
    )

    override fun getItemCount() = checkboxEntries.size

    override fun onBindViewHolder(holder: CheckBoxViewHolder, position: Int) {
        holder.bind(checkboxEntries[position])
    }
}

class CheckBoxViewHolder(
    view: View,
    private val checkedCallback: (CheckboxEntry) -> Unit,
    private val deleteCallback: (CheckboxEntry) -> Unit
): RecyclerView.ViewHolder(view) {
    private val check = view.findViewById<CheckBox>(R.id.checkboxEntry_check)!!
    private val delete = view.findViewById<ImageView>(R.id.checkboxEntry_delete)!!
    private val text = view.findViewById<TextInputEditText>(R.id.checkboxEntry_text)!!

    fun bind(checkboxEntry: CheckboxEntry) {
        check.isChecked = checkboxEntry.checked

        check.setOnCheckedChangeListener { _, isChecked -> checkedCallback(checkboxEntry) }
        delete.setOnClickListener {  deleteCallback(checkboxEntry) }
        text.setText(checkboxEntry.content)
        text.doAfterTextChanged { checkboxEntry.content = text.text.toString(); }
        text.setOnFocusChangeListener { _, hasFocus -> if(!hasFocus) println("Focus lost") }
    }
}
