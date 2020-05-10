package pw.cub3d.cub3_notes.ui.archived

import android.content.Context
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
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.archive_fragment.*
import pw.cub3d.cub3_notes.ui.MainActivity
import pw.cub3d.cub3_notes.core.dagger.injector
import pw.cub3d.cub3_notes.core.manager.SettingsManager
import pw.cub3d.cub3_notes.databinding.ArchiveFragmentBinding
import pw.cub3d.cub3_notes.ui.NoteLayoutManager
import pw.cub3d.cub3_notes.ui.NoteSelectionTrackerFactory
import pw.cub3d.cub3_notes.ui.bind
import pw.cub3d.cub3_notes.ui.home.ItemDetailsProvider
import pw.cub3d.cub3_notes.ui.home.MyItemKeyProvider
import pw.cub3d.cub3_notes.ui.home.NotesAdapter
import pw.cub3d.cub3_notes.ui.nav.NewNoteNavigationController
import javax.inject.Inject

class ArchiveFragment : Fragment() {
    private lateinit var binding: ArchiveFragmentBinding

    private val viewModel: ArchiveViewModel by viewModels { injector.archiveViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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

        archive_recycler.layoutManager = NoteLayoutManager(viewLifecycleOwner, viewModel.settingsManager)
        val adapter = NotesAdapter(requireContext()) { note, v  -> viewModel.noteNavigationController.editNote(findNavController(), note, v) }
        archive_recycler.adapter = adapter

        NoteSelectionTrackerFactory.buildTracker("archive-selection", archive_recycler).bind(adapter)

        viewModel.archivedNotes.observe(viewLifecycleOwner, Observer {
            adapter.updateData(it)
        })
    }
}
