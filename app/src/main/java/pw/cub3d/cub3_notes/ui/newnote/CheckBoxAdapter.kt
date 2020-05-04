package pw.cub3d.cub3_notes.ui.newnote

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.AutoCompleteManager
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.entity.CheckboxEntry
import pw.cub3d.cub3_notes.databinding.CheckboxEntryBinding

class CheckBoxAdapter(
    ctx: Context,
    var checkboxEntries: List<CheckboxEntry>,
    private val newNoteViewModel: NewNoteViewModel
) : RecyclerView.Adapter<CheckBoxViewHolder>() {
    private val layoutInflater = LayoutInflater.from(ctx)

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CheckBoxViewHolder(
        CheckboxEntryBinding.inflate(layoutInflater, parent, false),
        newNoteViewModel
    )

    override fun getItemId(position: Int): Long {
        return checkboxEntries[position].id
    }

    override fun getItemCount() = checkboxEntries.size

    override fun onBindViewHolder(holder: CheckBoxViewHolder, position: Int) {
        holder.bind(checkboxEntries[position])
    }

    fun updateData(checkboxes: List<CheckboxEntry>) {
        this.checkboxEntries = checkboxes
        notifyDataSetChanged()
    }
}

class CheckBoxViewHolder(
    val view: CheckboxEntryBinding,
    private val newNoteViewModel: NewNoteViewModel
): RecyclerView.ViewHolder(view.root) {

    fun bind(checkboxEntry: CheckboxEntry) {
        println("Bound check ${checkboxEntry}")
        view.checkboxEntryCheck.isChecked = checkboxEntry.checked

        view.checkboxEntryDelete.setOnClickListener {
            println("check delete $checkboxEntry")
            newNoteViewModel.onCheckboxDelete(checkboxEntry)
        }

        view.checkboxEntryCheck.setOnCheckedChangeListener { _, isChecked ->
            println("check updated $checkboxEntry to $isChecked")
            newNoteViewModel.onCheckboxChecked(checkboxEntry, isChecked)
        }

        view.checkboxEntryText.setText(checkboxEntry.content)
        view.checkboxEntryText.setAdapter(ArrayAdapter<String>(view.root.context, android.R.layout.simple_list_item_1, AutoCompleteManager(view.root.context).food))
        view.checkboxEntryText.doAfterTextChanged { it ->
            println("Text changed to: $it")
            newNoteViewModel.onCheckboxTextChange(checkboxEntry, it.toString())
        }
    }
}
