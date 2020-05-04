package pw.cub3d.cub3_notes.ui.newnote

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_new_note.*
import kotlinx.coroutines.runBlocking
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.entity.Note
import pw.cub3d.cub3_notes.databinding.FragmentNewNoteBinding
import pw.cub3d.cub3_notes.ui.dialog.reminderdialog.ReminderDialog
import pw.cub3d.cub3_notes.ui.nav.NewNoteNavigationController
import pw.cub3d.cub3_notes.ui.noteLabels.NoteLabelEditFragment
import javax.inject.Inject

class NewNoteFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: NewNoteViewModelFactory
    private val viewModel: NewNoteViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        arguments?.let {
            it.getBoolean(NewNoteNavigationController.KEY_NEW_NOTE, false).takeIf { it }?.let { runBlocking { viewModel.newNote().await() } }
            it.getLong(NewNoteNavigationController.KEY_NOTE, -1).takeIf { it != -1L }?.let { runBlocking { viewModel.loadNote(it).await() } }
            it.getString(NewNoteNavigationController.KEY_NOTE_TYPE)?.let { type -> viewModel.setNoteType(type) }
            it.getString(NewNoteNavigationController.KEY_NOTE_IMAGE_PATH)?.let { imagePath -> viewModel.addImage(imagePath) }
        }

        val binding = FragmentNewNoteBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.type.observe(viewLifecycleOwner, Observer { type ->
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

        viewModel.modificationTime.observe(viewLifecycleOwner, Observer {
            createNote_lastEdited.text = it
        })

        viewModel.checkboxes.observe(viewLifecycleOwner, Observer {
            println("Updating checkboxes: $it")
            createNote_checkBoxes.layoutManager = LinearLayoutManager(requireContext())
            createNote_checkBoxes.adapter = CheckBoxAdapter(requireContext(), it, { viewModel.saveCheckbox(it) }, { viewModel.onCheckboxDelete(it) })
        })

        viewModel.defaultNoteColours.observe(viewLifecycleOwner, Observer {
            println("Got colours: $it")
            createNote_more_colors.layoutManager = LinearLayoutManager(requireContext())
            createNote_more_colors.adapter = ColoursAdapter(requireContext(), it) {
                viewModel.setNoteColour(it.hex_colour)
            }
        })

        viewModel.images.observe(viewLifecycleOwner, Observer {
            it.firstOrNull()?.let {
                Glide.with(this@NewNoteFragment)
                    .load(it.getFile(requireContext()))
                    .into(createNote_image)
            }
        })

        viewModel.title.distinctUntilChanged().ignoreFirstValue().observe(viewLifecycleOwner, Observer {
            viewModel.onTitleChange(it)
        })

        viewModel.text.distinctUntilChanged().ignoreFirstValue().observe(viewLifecycleOwner, Observer {
            viewModel.onTextChange(it)
        })

        viewModel.pinned.distinctUntilChanged().ignoreFirstValue().observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), it.takeIf { it }?.let {  "Pinned" } ?: "Unpinned", Toast.LENGTH_SHORT).show()
        })

        createNote_back.setOnClickListener { findNavController(this@NewNoteFragment).navigate(R.id.nav_home) }
        createNote_pin.setOnClickListener { viewModel.onPin() }
        createNote_archive.setOnClickListener {
            viewModel.onArchive()
            findNavController().popBackStack()
        }

        createNote_newItem.setOnClickListener { viewModel.addCheckbox() }

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

        createNote_reminder.setOnClickListener {
            ReminderDialog(requireActivity()).show()
        }

        createNote_more_labels.setOnClickListener {
            findNavController(this@NewNoteFragment).navigate(R.id.action_nav_new_note_to_nav_note_label_edit, Bundle().apply {
                putLong(NoteLabelEditFragment.KEY_NOTE_ID, viewModel.noteAndCheckboxes!!.value!!.note.id)
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

fun <T> LiveData<T>.distinctUntilChanged() = Transformations.distinctUntilChanged(this)

fun <T> LiveData<T>.ignoreFirstValue() = ignoreFirstAssignment(this)