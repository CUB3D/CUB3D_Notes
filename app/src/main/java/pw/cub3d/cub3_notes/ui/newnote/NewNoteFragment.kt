package pw.cub3d.cub3_notes.ui.newnote

import android.content.Context
import android.graphics.Color
import android.graphics.Color.WHITE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_new_note.*
import kotlinx.coroutines.runBlocking
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.entity.CheckboxEntry
import pw.cub3d.cub3_notes.database.entity.Colour
import pw.cub3d.cub3_notes.database.entity.Note
import pw.cub3d.cub3_notes.databinding.FragmentNewNoteBinding
import pw.cub3d.cub3_notes.ui.dialog.reminderdialog.ReminderDialog
import pw.cub3d.cub3_notes.ui.nav.NewNoteNavigationController
import pw.cub3d.cub3_notes.ui.noteLabels.NoteLabelEditFragment
import javax.inject.Inject


class NewNoteFragment : Fragment() {

    @Inject
    lateinit var newNoteViewModelFactory: NewNoteViewModelFactory
    private lateinit var newNoteViewModel: NewNoteViewModel
    private lateinit var binding: FragmentNewNoteBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        newNoteViewModel = ViewModelProvider(viewModelStore, newNoteViewModelFactory)
            .get(NewNoteViewModel::class.java)

        arguments?.let {
            it.getBoolean(NewNoteNavigationController.KEY_NEW_NOTE, false).takeIf { it }?.let { runBlocking { newNoteViewModel.newNote().await() } }
            it.getLong(NewNoteNavigationController.KEY_NOTE, -1).takeIf { it != -1L }?.let { runBlocking { newNoteViewModel.loadNote(it).await() } }
            it.getString(NewNoteNavigationController.KEY_NOTE_TYPE)?.let { type -> newNoteViewModel.setNoteType(type) }
            it.getString(NewNoteNavigationController.KEY_NOTE_IMAGE_PATH)?.let { imagePath -> newNoteViewModel.addImage(imagePath) }
        }

        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_new_note, container, false)

        binding.viewModel = newNoteViewModel
        binding.noteColour = Color.WHITE
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        newNoteViewModel.loadNote(arguments?.getLong(NewNoteNavigationController.KEY_NOTE, -1) ?: -1)

        newNoteViewModel.type.observe(viewLifecycleOwner, Observer { type ->
            if(type == Note.TYPE_CHECKBOX) {
                createNote_text.visibility = View.GONE
                createNote_checkBoxes.visibility = View.VISIBLE
                createNote_newItem.visibility = View.VISIBLE
            } else if(type == Note.TYPE_TEXT) {
                createNote_text.visibility = View.VISIBLE
                createNote_checkBoxes.visibility = View.GONE
                createNote_newItem.visibility = View.GONE
            }
        })

        newNoteViewModel.modificationTime.observe(viewLifecycleOwner, Observer {
            createNote_lastEdited.text = it
        })

        //TODO sort in room junction
        newNoteViewModel.checkboxes.map { it.sortedBy { it.position } }.distinctUntilChanged { old, new -> old.map { it.id } == new.map { it.id } }.observe(viewLifecycleOwner, Observer { checkboxes ->
            println("Updating checkboxes: $checkboxes")
            createNote_checkBoxes.layoutManager = LinearLayoutManager(requireContext())
            createNote_checkBoxes.adapter = CheckBoxAdapter(requireContext(), checkboxes, newNoteViewModel)

            val callback: ItemTouchHelper.Callback = object: ItemTouchHelper.Callback() {
                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    val cb: (vh: RecyclerView.ViewHolder)->CheckboxEntry = {vh -> checkboxes.find { it.id ==  vh.itemId}!!}

                    return makeMovementFlags(ItemTouchHelper.UP.or(ItemTouchHelper.DOWN), if(cb(viewHolder).checked) ItemTouchHelper.RIGHT else ItemTouchHelper.LEFT)
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val cb: (vh: RecyclerView.ViewHolder)->CheckboxEntry = {vh -> checkboxes.find { it.id ==  vh.itemId}!!}

                    newNoteViewModel.upadateCheckboxPosition(cb(viewHolder), cb(target).position)
                    newNoteViewModel.upadateCheckboxPosition(cb(target), cb(viewHolder).position)

//                    val moveUp = target.adapterPosition > viewHolder.adapterPosition
//
//                    if(moveUp) {
//                        newNoteViewModel.upadateCheckboxPosition(checkboxes[viewHolder.adapterPosition], checkboxes[target.adapterPosition].position)
//                        newNoteViewModel.upadateCheckboxPosition(checkboxes[target.adapterPosition], checkboxes[viewHolder.adapterPosition].position)
//
//                    } else {
//                        newNoteViewModel.moveCheckbox(checkboxes[viewHolder.adapterPosition], ItemTouchHelper.DOWN)
//                    }

//                    //TODO only update the ones that are needed
//                    newNoteViewModel.upadateCheckboxPosition(checkboxes[viewHolder.adapterPosition], target.adapterPosition)
//
//                    (viewHolder.adapterPosition until (createNote_checkBoxes.adapter as CheckBoxAdapter).itemCount).forEachIndexed { ind, it ->
//                        newNoteViewModel.upadateCheckboxPosition(checkboxes[it], target.adapterPosition + ind + 1)
//                    }

                    (createNote_checkBoxes.adapter as CheckBoxAdapter).notifyDataSetChanged()


//                    Toast.makeText(requireContext(), "Moving item", Toast.LENGTH_LONG).show()
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if(direction == ItemTouchHelper.LEFT) {
                        newNoteViewModel.onCheckboxChecked(checkboxes[viewHolder.adapterPosition], true)
                        //TODO: is this the correct pos
                        (createNote_checkBoxes.adapter as CheckBoxAdapter).notifyDataSetChanged()
                    }

                    if(direction == ItemTouchHelper.RIGHT) {
                        newNoteViewModel.onCheckboxChecked(checkboxes[viewHolder.adapterPosition], false)
                        //TODO: is this the correct pos
                        (createNote_checkBoxes.adapter as CheckBoxAdapter).notifyDataSetChanged()
                    }
                }

            }
            val touchHelper = ItemTouchHelper(callback)
            touchHelper.attachToRecyclerView(createNote_checkBoxes)
        })

        newNoteViewModel.defaultNoteColours.observe(viewLifecycleOwner, Observer {
            createNote_more_colors.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            createNote_more_colors.adapter = ColoursAdapter(this, it, newNoteViewModel)
        })

        newNoteViewModel.images.observe(viewLifecycleOwner, Observer {
            it.firstOrNull()?.let {
                Glide.with(this@NewNoteFragment)
                    .load(it.getFile(requireContext()))
                    .into(createNote_image)
            }
        })

        newNoteViewModel.title.distinctUntilChanged().ignoreFirstValue().observe(viewLifecycleOwner, Observer {
            newNoteViewModel.onTitleChange(it)
        })

        newNoteViewModel.text.distinctUntilChanged().ignoreFirstValue().observe(viewLifecycleOwner, Observer {
            newNoteViewModel.onTextChange(it)
        })

        newNoteViewModel.pinned.distinctUntilChanged().ignoreFirstValue().observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), it.takeIf { it }?.let {  "Pinned" } ?: "Unpinned", Toast.LENGTH_SHORT).show()
        })

        newNoteViewModel.colour.observe(viewLifecycleOwner, Observer {
            binding.noteColour = it
        })

        createNote_back.setOnClickListener { findNavController(this@NewNoteFragment).navigate(R.id.nav_home) }
        createNote_pin.setOnClickListener { newNoteViewModel.onPin() }
        createNote_archive.setOnClickListener {
            newNoteViewModel.onArchive()
            findNavController().popBackStack()
        }

        createNote_newItem.setOnClickListener { newNoteViewModel.addCheckbox() }

        BottomSheetBehavior.from(createNote_moreSheet).state = BottomSheetBehavior.STATE_HIDDEN

        createNote_more.setOnClickListener {
            BottomSheetBehavior.from(createNote_moreSheet).apply {
                state = if(state == BottomSheetBehavior.STATE_HIDDEN) {
                    BottomSheetBehavior.STATE_EXPANDED
                } else {
                    BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }

        createNote_reminder.setOnClickListener { ReminderDialog(requireActivity()).show() }

        createNote_more_delete.setOnClickListener {
            newNoteViewModel.onDelete()
            findNavController().popBackStack()
        }

        createNote_more_labels.setOnClickListener {
            findNavController(this@NewNoteFragment).navigate(R.id.action_nav_new_note_to_nav_note_label_edit, Bundle().apply {
                putLong(NoteLabelEditFragment.KEY_NOTE_ID, newNoteViewModel.noteAndCheckboxes!!.value!!.note.id)
            })
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}

fun <T> ignoreFirstAssignment(data: LiveData<T>): LiveData<T> = MediatorLiveData<T>().apply {
    var updates = 0
    addSource(data) {
        if(updates > 0) {
            value = it
        }
        updates += 1
    }
}

fun <T> distinctUntilLengthChanges(data: LiveData<List<T>>): LiveData<List<T>> = MediatorLiveData<List<T>>().apply {
    addSource(data) {
        if(value == null || value!!.size != it.size) {
            value = it
        }
    }
}

fun <T> distinctUntilChangedPred(data:  LiveData<T>, predicate: (old: T, new: T)->Boolean) = MediatorLiveData<T>().apply {
    addSource(data) {
        if(value != null && it != null) {
            if(!predicate(value!!, it)) {
                value = it
            }
        } else {
            value = it
        }
    }
}

fun <T> LiveData<T>.distinctUntilChanged() = Transformations.distinctUntilChanged(this)
fun <T> LiveData<T>.distinctUntilChanged(predicate: (old: T, new: T) -> Boolean) = distinctUntilChangedPred(this, predicate)

fun <T> LiveData<T>.ignoreFirstValue() = ignoreFirstAssignment(this)

fun <T> LiveData<List<T>>.distinctUntilLengthChanged() = distinctUntilLengthChanges(this)

fun <T, Y> LiveData<T>.map(func: (T) -> Y) = Transformations.map(this, func)