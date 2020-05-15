package pw.cub3d.cub3_notes.ui.newnote

import android.content.Context
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.core.database.entity.CheckboxEntry
import pw.cub3d.cub3_notes.core.manager.AutoCompleteManager
import pw.cub3d.cub3_notes.databinding.CheckboxEntryBinding
import pw.cub3d.cub3_notes.ui.home.BaseAdapter

class CheckBoxAdapter(
    ctx: Context,
    private val newNoteViewModel: NewNoteViewModel
): BaseAdapter<CheckboxEntry, CheckBoxViewHolder>(ctx) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CheckBoxViewHolder(
        CheckboxEntryBinding.inflate(layoutInflater, parent, false),
        newNoteViewModel
    )

    override fun getItemId(position: Int) = getItem(position).id

    override fun onBindViewHolder(holder: CheckBoxViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class CheckBoxViewHolder(
    val view: CheckboxEntryBinding,
    private val newNoteViewModel: NewNoteViewModel
): RecyclerView.ViewHolder(view.root) {

    var enabled = false

    fun bind(checkboxEntry: CheckboxEntry) {
        println("Bound check ${checkboxEntry}")

        enabled = false

        view.checkboxEntryCheck.isChecked = checkboxEntry.checked

        view.checkboxEntryDelete.setOnClickListener {
            println("check delete $checkboxEntry")
            newNoteViewModel.onCheckboxDelete(checkboxEntry)
        }

        view.checkboxEntryCheck.setOnCheckedChangeListener { _, isChecked ->
            if (!enabled) { println("Ignoring check change"); return@setOnCheckedChangeListener}

            println("check updated $checkboxEntry to $isChecked")
            newNoteViewModel.onCheckboxChecked(checkboxEntry, isChecked)
        }

        view.checkboxEntryText.setText(checkboxEntry.content)
        view.checkboxEntryText.setAdapter(ArrayAdapter<String>(view.root.context, android.R.layout.simple_list_item_1, AutoCompleteManager(
            view.root.context
        ).food))
        view.checkboxEntryText.doAfterTextChanged { it ->
            if (!enabled) { println("Ignoring text change"); return@doAfterTextChanged}
            println("Text changed to: $it")
            newNoteViewModel.onCheckboxTextChange(checkboxEntry, it.toString())
        }

        enabled = true
    }
}
