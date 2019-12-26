package pw.cub3d.cub3_notes.ui.newnote

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_new_note.*
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.entity.Note
import pw.cub3d.cub3_notes.ui.nav.NewNoteNavigationController
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
        newNoteViewModel =
            ViewModelProviders.of(this, newNoteViewModelFactory).get(NewNoteViewModel::class.java)
//
//        val binding: FragmentNewNoteBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_new_note, container, false)
//
//        binding.newNoteViewModel = newNoteViewModel

        return inflater.inflate(R.layout.fragment_new_note, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            it.getParcelable<Note>(NewNoteNavigationController.KEY_NOTE)?.let { note ->
                println("Editing note: $note")
                newNoteViewModel.setNote(note)
                createNote_text.setText(note.text)
                createNote_title.setText(note.title)
            }

            it.getString(NewNoteNavigationController.KEY_NOTE_TYPE)?.let { type -> newNoteViewModel.setNoteType(type) }
        }

        newNoteViewModel.modificationTime.observe(this, Observer {
            createNote_lastEdited.text = it
        })

        createNote_text.doAfterTextChanged {
            newNoteViewModel.onNoteTextChanged(it.toString())
        }

        createNote_title.doAfterTextChanged {
            newNoteViewModel.onNoteTitleChanged(it.toString())
        }

        newNoteViewModel.type.observe(this, Observer { type ->
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

        newNoteViewModel.checkboxes.observe(this, Observer {
            println("Updating checkboxes: $it")
            createNote_checkBoxes.layoutManager = LinearLayoutManager(requireContext())
            createNote_checkBoxes.adapter = CheckBoxAdapter(requireContext(), it, { newNoteViewModel.onCheckboxChecked(it) }, { newNoteViewModel.onCheckboxDelete(it) })
        })

        createNote_back.setOnClickListener { findNavController(this@NewNoteFragment).navigate(R.id.nav_home) }
        createNote_pin.setOnClickListener { newNoteViewModel.onPin() }
        createNote_archive.setOnClickListener {
            newNoteViewModel.onArchive()
            findNavController().popBackStack()
        }

        createNote_newItem.setOnClickListener { newNoteViewModel.addCheckbox() }
    }

    override fun onPause() {
        super.onPause()
        newNoteViewModel.save()
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}