package pw.cub3d.cub3_notes.ui.noteLabels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_note_label_edit.*
import pw.cub3d.cub3_notes.core.dagger.injector
import pw.cub3d.cub3_notes.databinding.FragmentNoteLabelEditBinding

class NoteLabelEditFragment : Fragment() {

    private val viewModel: NoteLabelEditViewModel by viewModels { injector.noteLabelEditViewModelFactory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = FragmentNoteLabelEditBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadNote(arguments?.getLong(KEY_NOTE_ID, -1) ?: -1)

        viewModel.labels.observe(viewLifecycleOwner, Observer {
            println("New labels: $it")

            noteLabelEdit_recycler.layoutManager = LinearLayoutManager(requireContext())
            noteLabelEdit_recycler.adapter = NoteLabelEditAdapter(requireContext(), it, viewModel.labelDao, viewModel.noteId.value!!)
        })
    }

    companion object {
        const val KEY_NOTE_ID = "note_id"
    }
}
