package pw.cub3d.cub3_notes.ui.deleted

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import dagger.android.support.AndroidSupportInjection

import pw.cub3d.cub3_notes.core.manager.SettingsManager
import pw.cub3d.cub3_notes.core.dagger.injector
import pw.cub3d.cub3_notes.databinding.FragmentDeletedNotesBinding
import pw.cub3d.cub3_notes.ui.*
import pw.cub3d.cub3_notes.ui.home.ItemDetailsProvider
import pw.cub3d.cub3_notes.ui.home.MyItemKeyProvider
import pw.cub3d.cub3_notes.ui.home.NotesAdapter
import pw.cub3d.cub3_notes.ui.nav.NewNoteNavigationController
import javax.inject.Inject


class DeletedNotesFragment : Fragment() {
    val viewModel: DeletedNotesViewModel by viewModels { injector.deleteNoteViewModelFactory() }

    lateinit var binding: FragmentDeletedNotesBinding

    lateinit var adapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentDeletedNotesBinding.inflate(inflater, container, false).apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.deletedToolbar)
        (requireActivity() as AppCompatActivity).setupActionBarWithNavController(findNavController(), (requireActivity() as MainActivity).appBarConfiguration)
        (requireActivity() as AppCompatActivity).supportActionBar!!.title = "Deleted"


        binding.deletedRecycler.layoutManager = NoteLayoutManager(viewLifecycleOwner, viewModel.settingsManager)
        adapter = NotesAdapter(requireContext()) { note, v -> viewModel.noteNavigationController.editNote(findNavController(), note, v) }
        binding.deletedRecycler.adapter = adapter

        NoteSelectionTrackerFactory.buildTracker("deleted-selection", binding.deletedRecycler).bind(adapter)

        viewModel.deletedNotes.observe(viewLifecycleOwner, Observer {
            adapter.updateData(it)
        })
    }
}