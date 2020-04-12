package pw.cub3d.cub3_notes.ui.newnote

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.AutoCompleteManager
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.entity.CheckboxEntry
import pw.cub3d.cub3_notes.databinding.CheckboxEntryBinding

class CheckBoxAdapter(
    ctx: Context,
    private val checkboxEntries: List<CheckboxEntry>,
    private val newNoteViewModel: NewNoteViewModel
) : RecyclerView.Adapter<CheckBoxViewHolder>() {
    private val layoutInflater = LayoutInflater.from(ctx)

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CheckBoxViewHolder(
        DataBindingUtil.inflate(layoutInflater, R.layout.checkbox_entry, parent, false),
        newNoteViewModel
    )

    override fun getItemId(position: Int): Long {
        return checkboxEntries[position].id
    }

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

        view.checkboxEntryText.setText(checkboxEntry.content)

        view.checkboxEntryText.setAdapter(ArrayAdapter<String>(view.root.context, android.R.layout.simple_list_item_1, AutoCompleteManager(view.root.context).food))

        view.checkboxEntryText.doOnTextChanged { text, start, count, after ->
            newNoteViewModel.onCheckboxTextChange(checkboxEntry, text.toString())
        }

//        val test = MutableLiveData<String>()
//        view.content = test
//        test.postValue(checkboxEntry.content)
//
//        //TODO: pass lifecycleowner and use observe
//        test.distinctUntilChanged().ignoreFirstValue().observeForever {
//            println("Got new content for $checkboxEntry, $it")
//            newNoteViewModel.onCheckboxTextChange(checkboxEntry, it)
//        }
    }
}
