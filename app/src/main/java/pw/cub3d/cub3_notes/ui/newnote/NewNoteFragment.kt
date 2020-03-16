package pw.cub3d.cub3_notes.ui.newnote

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModelProvider
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
import pw.cub3d.cub3_notes.ui.nav.NewNoteNavigationController
import pw.cub3d.cub3_notes.ui.noteLabels.NoteLabelEditFragment
import pw.cub3d.cub3_notes.ui.dialog.reminderdialog.ReminderDialog
import javax.inject.Inject

class NewNoteFragment : Fragment() {

    @Inject
    lateinit var newNoteViewModelFactory: NewNoteViewModelFactory
    private lateinit var newNoteViewModel: NewNoteViewModel

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

        val binding: FragmentNewNoteBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_new_note, container, false)

        binding.viewModel = newNoteViewModel
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

        newNoteViewModel.checkboxes.observe(viewLifecycleOwner, Observer {
            println("Updating checkboxes: $it")
            createNote_checkBoxes.layoutManager = LinearLayoutManager(requireContext())
            createNote_checkBoxes.adapter = CheckBoxAdapter(requireContext(), it, { newNoteViewModel.saveCheckbox(it) }, { newNoteViewModel.onCheckboxDelete(it) })
        })

        newNoteViewModel.defaultNoteColours.observe(viewLifecycleOwner, Observer {
            println("Got colours: $it")
            createNote_more_colors.layoutManager = LinearLayoutManager(requireContext())
            createNote_more_colors.adapter = ColoursAdapter(requireContext(), it) {
                newNoteViewModel.setNoteColour(it.hex_colour)
            }
        })

        newNoteViewModel.images.observe(viewLifecycleOwner, Observer {
            it.firstOrNull()?.let {
                Glide.with(this@NewNoteFragment)
                    .load(it.getFile(requireContext()))
                    .into(createNote_image)
            }
        })

        Transformations.distinctUntilChanged(newNoteViewModel.title).observe(viewLifecycleOwner, Observer {
            newNoteViewModel.onTitleChange(it)
        })

        Transformations.distinctUntilChanged(newNoteViewModel.text).observe(viewLifecycleOwner, Observer {
            newNoteViewModel.onTextChange(it)
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

        createNote_reminder.setOnClickListener {
            ReminderDialog(requireActivity()).show()
        }

        createNote_more_labels.setOnClickListener {
            findNavController(this@NewNoteFragment).navigate(R.id.action_nav_new_note_to_nav_note_label_edit, Bundle().apply {
                putLong(NoteLabelEditFragment.KEY_NOTE_ID, newNoteViewModel.noteAndCheckboxes!!.value!!.note.id)
            })
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}