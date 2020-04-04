package pw.cub3d.cub3_notes.ui.newnote

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.entity.CheckboxEntry
import pw.cub3d.cub3_notes.databinding.CheckboxEntryBinding

class CheckBoxAdapter(
    ctx: Context,
    private val checkboxEntries: List<CheckboxEntry>,
    private val newNoteViewModel: NewNoteViewModel
) : RecyclerView.Adapter<CheckBoxViewHolder>() {
    private val layoutInflater = LayoutInflater.from(ctx)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CheckBoxViewHolder(
        DataBindingUtil.inflate(layoutInflater, R.layout.checkbox_entry, parent, false),
        newNoteViewModel
    )

    override fun getItemCount() = checkboxEntries.size

    override fun onBindViewHolder(holder: CheckBoxViewHolder, position: Int) {
        holder.bind(checkboxEntries[position])
    }
}

class CheckBoxViewHolder(
    private val view: CheckboxEntryBinding,
    private val newNoteViewModel: NewNoteViewModel
): RecyclerView.ViewHolder(view.root) {

    fun bind(checkboxEntry: CheckboxEntry) {
        view.entry = checkboxEntry
        view.checkboxEntryCheck.isChecked = checkboxEntry.checked
        println("Bound checkbox")

        view.checkboxEntryDelete.setOnClickListener {
            newNoteViewModel.onCheckboxDelete(checkboxEntry)
        }

        view.checkboxEntryCheck.setOnCheckedChangeListener { _, isChecked ->
            newNoteViewModel.onCheckboxChecked(checkboxEntry, isChecked)
        }

//        view.isChecked = MutableLiveData<Boolean>(checkboxEntry.checked).apply {
//            observeForever {
//                checkboxEntry.checked = this.value!!
//                saveCallback(checkboxEntry)
//            }
//        }
//
//        view.checkboxEntryDelete.setOnClickListener {  deleteCallback(checkboxEntry) }
//        view.checkboxEntryText.doAfterTextChanged {  saveCallback(checkboxEntry) }
    }
}
