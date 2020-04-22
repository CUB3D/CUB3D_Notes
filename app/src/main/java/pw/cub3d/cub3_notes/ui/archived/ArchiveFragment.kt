package pw.cub3d.cub3_notes.ui.archived

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.archive_fragment.*
import kotlinx.android.synthetic.main.fragment_home.*

import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.activity.MainActivity
import pw.cub3d.cub3_notes.databinding.ArchiveFragmentBinding
import pw.cub3d.cub3_notes.databinding.ArchiveFragmentBindingImpl
import pw.cub3d.cub3_notes.databinding.FragmentDeletedNotesBinding
import pw.cub3d.cub3_notes.ui.home.ItemDetailsProvider
import pw.cub3d.cub3_notes.ui.home.MyItemKeyProvider
import pw.cub3d.cub3_notes.ui.home.NoteViewHolder
import pw.cub3d.cub3_notes.ui.home.NotesAdapter
import pw.cub3d.cub3_notes.ui.nav.NewNoteNavigationController
import javax.inject.Inject

class ArchiveFragment : Fragment() {
    private lateinit var binding: ArchiveFragmentBinding

    @Inject lateinit var archiveViewModelFactory: ArchiveViewModelFactory
    private lateinit var viewModel: ArchiveViewModel

    @Inject lateinit var noteNavigationController: NewNoteNavigationController


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

        viewModel = ViewModelProvider(this, archiveViewModelFactory).get(ArchiveViewModel::class.java)

        archive_recycler.layoutManager = GridLayoutManager(requireContext(), 2)
        archive_recycler.adapter = NotesAdapter(requireContext(), emptyList()) { note -> noteNavigationController.editNote(findNavController(), note) }

        val keyProvider = MyItemKeyProvider(archive_recycler)

        val tracker = SelectionTracker.Builder(
            "archive-selection",
            archive_recycler,
            keyProvider,
            ItemDetailsProvider(archive_recycler, keyProvider),
            StorageStrategy.createLongStorage()
        )
            .withSelectionPredicate(SelectionPredicates.createSelectAnything())
            .build()

        (archive_recycler.adapter as NotesAdapter).selectionTracker = tracker

        viewModel.archivedNotes.observe(viewLifecycleOwner, Observer {
            (archive_recycler.adapter as NotesAdapter).updateData(it)
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }
}
