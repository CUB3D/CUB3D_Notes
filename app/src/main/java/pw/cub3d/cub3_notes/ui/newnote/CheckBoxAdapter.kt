package pw.cub3d.cub3_notes.ui.newnote

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import pw.cub3d.cub3_notes.core.database.entity.CheckboxEntry
import pw.cub3d.cub3_notes.core.manager.AutoCompleteManager
import pw.cub3d.cub3_notes.databinding.CheckboxEntryBinding
import pw.cub3d.cub3_notes.ui.home.BaseAdapter

class CheckBoxAdapter(
    ctx: Context,
    private val newNoteViewModel: NewNoteViewModel,
    val lifecycleOwner: LifecycleOwner
) : BaseAdapter<CheckboxEntry, CheckBoxViewHolder>(ctx) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CheckBoxViewHolder(
        CheckboxEntryBinding.inflate(layoutInflater, parent, false),
        newNoteViewModel,
        lifecycleOwner
    )

    override fun getItemId(position: Int) = getItem(position).id

    override fun onBindViewHolder(holder: CheckBoxViewHolder, position: Int) {
        val lastItem = position == itemCount - 1
        holder.bind(getItem(position), lastItem)
    }
}

class CheckBoxViewHolder(
    val view: CheckboxEntryBinding,
    private val newNoteViewModel: NewNoteViewModel,
    val lifecycleOwner: LifecycleOwner
) : RecyclerView.ViewHolder(view.root) {

    init {
        view.checkboxEntryText.setAdapter(ArrayAdapter(view.root.context, android.R.layout.simple_list_item_1, AutoCompleteManager(
            view.root.context
        ).food))

        view.text = MutableLiveData()
        view.checked = MutableLiveData()
    }

    fun bind(
        checkboxEntry: CheckboxEntry,
        lastItem: Boolean
    ) {
        view.checkboxEntryCheckAnimation.visibility = View.GONE
        view.checkboxEntryDeleteAnimation.visibility = View.GONE

        view.text!!.removeObservers(lifecycleOwner)
        view.text = MutableLiveData(checkboxEntry.content)
        view.text!!.observe(lifecycleOwner, Observer {
            newNoteViewModel.onCheckboxTextChange(checkboxEntry, it)
        })

        view.checked!!.removeObservers(lifecycleOwner)
        view.checked = MutableLiveData(checkboxEntry.checked)
        view.checked!!.observe(lifecycleOwner, Observer {
            newNoteViewModel.onCheckboxChecked(checkboxEntry, it)
        })

        view.checkboxEntryDelete.setOnClickListener {
            newNoteViewModel.onCheckboxDelete(checkboxEntry)
        }

        if (lastItem) {
            view.checkboxEntryText.requestFocus()
        }
    }
}
