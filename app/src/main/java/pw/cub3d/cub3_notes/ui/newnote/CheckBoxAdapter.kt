package pw.cub3d.cub3_notes.ui.newnote

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.entity.CheckboxEntry
import pw.cub3d.cub3_notes.databinding.CheckboxEntryBinding

class CheckBoxAdapter(
    ctx: Context,
    private val checkboxEntries: List<CheckboxEntry>,
    private val saveCallback: (CheckboxEntry)->Unit,
    private val deleteCallback: (CheckboxEntry)->Unit
) : RecyclerView.Adapter<CheckBoxViewHolder>() {
    private val layoutInflater = LayoutInflater.from(ctx)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CheckBoxViewHolder(
        DataBindingUtil.inflate(layoutInflater, R.layout.checkbox_entry, parent, false),
        saveCallback,
        deleteCallback
    )

    override fun getItemCount() = checkboxEntries.size

    override fun onBindViewHolder(holder: CheckBoxViewHolder, position: Int) {
        holder.bind(checkboxEntries[position])
    }
}

class CheckBoxViewHolder(
    private val view: CheckboxEntryBinding,
    private val saveCallback: (CheckboxEntry) -> Unit,
    private val deleteCallback: (CheckboxEntry) -> Unit
): RecyclerView.ViewHolder(view.root) {

    fun bind(checkboxEntry: CheckboxEntry) {
        view.entry = checkboxEntry
        view.isChecked = MutableLiveData<Boolean>(checkboxEntry.checked).apply {
            observeForever {
                checkboxEntry.checked = this.value!!
                saveCallback(checkboxEntry)
            }
        }

        view.checkboxEntryDelete.setOnClickListener {  deleteCallback(checkboxEntry) }
        view.checkboxEntryText.doAfterTextChanged {  saveCallback(checkboxEntry) }
    }
}
