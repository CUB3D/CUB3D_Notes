package pw.cub3d.cub3_notes.ui.archived

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import pw.cub3d.cub3_notes.core.dagger.injector
import pw.cub3d.cub3_notes.databinding.ArchiveFragmentBinding
import pw.cub3d.cub3_notes.ui.MainActivity
import pw.cub3d.cub3_notes.ui.NoteLayoutManager
import pw.cub3d.cub3_notes.ui.NoteSelectionTrackerFactory
import pw.cub3d.cub3_notes.ui.bind
import pw.cub3d.cub3_notes.ui.home.NotesAdapter

class ArchiveFragment : Fragment() {
    private lateinit var binding: ArchiveFragmentBinding

    private val viewModel: ArchiveViewModel by viewModels { injector.archiveViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ArchiveFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.archiveToolbar)
        (requireActivity() as AppCompatActivity).setupActionBarWithNavController(findNavController(), (requireActivity() as MainActivity).appBarConfiguration)
        (requireActivity() as AppCompatActivity).supportActionBar!!.title = "Archive"

        binding.archiveRecycler.layoutManager = NoteLayoutManager(viewLifecycleOwner, viewModel.settingsManager)
        val adapter = NotesAdapter(requireContext()) { note, v  -> viewModel.noteNavigationController.editNote(findNavController(), note, v) }
        binding.archiveRecycler.adapter = adapter

        NoteSelectionTrackerFactory.buildTracker("archive-selection", binding.archiveRecycler).bind(adapter)

        viewModel.archivedNotes.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }
}
