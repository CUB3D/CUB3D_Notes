package pw.cub3d.cub3_notes.ui.noteLabels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import pw.cub3d.cub3_notes.core.dagger.injector
import pw.cub3d.cub3_notes.databinding.FragmentNoteLabelEditBinding

class NoteLabelEditFragment : Fragment() {

    private lateinit var binding: FragmentNoteLabelEditBinding
    private val viewModel: NoteLabelEditViewModel by viewModels { injector.noteLabelEditViewModelFactory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentNoteLabelEditBinding.inflate(inflater, container, false).apply {
            viewModel = viewModel
            binding = this
        }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadNote(arguments?.getLong(KEY_NOTE_ID, -1) ?: -1)

        binding.noteLabelEditRecycler.layoutManager = LinearLayoutManager(requireContext())
        val labelAdapter = NoteLabelEditAdapter(requireContext(), viewModel.labelDao, viewModel.noteId.value!!)
        binding.noteLabelEditRecycler.adapter = labelAdapter
        viewModel.labels.observe(viewLifecycleOwner, Observer { labelAdapter.submitList(it) })
    }

    companion object {
        const val KEY_NOTE_ID = "note_id"
    }
}
