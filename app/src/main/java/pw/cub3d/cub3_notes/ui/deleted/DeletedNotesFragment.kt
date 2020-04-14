package pw.cub3d.cub3_notes.ui.deleted

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_home.*

import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.SettingsManager
import pw.cub3d.cub3_notes.activity.MainActivity
import pw.cub3d.cub3_notes.databinding.FragmentDeletedNotesBinding
import pw.cub3d.cub3_notes.ui.home.MyItemKeyProvider
import pw.cub3d.cub3_notes.ui.home.NoteViewHolder
import pw.cub3d.cub3_notes.ui.home.NotesAdapter
import pw.cub3d.cub3_notes.ui.nav.NewNoteNavigationController
import javax.inject.Inject


class DeletedNotesFragment : Fragment() {
    @Inject lateinit var viewModelFactory: DeletedNotesViewModelFactory
    val viewModel: DeletedNotesViewModel by viewModels { viewModelFactory }
    lateinit var binding: FragmentDeletedNotesBinding

    @Inject lateinit var navigationController: NewNoteNavigationController
    @Inject lateinit var settingsManager: SettingsManager

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

        settingsManager.noteLayout.observe(viewLifecycleOwner, Observer {
            (binding.deletedRecycler.layoutManager as StaggeredGridLayoutManager).apply {
                spanCount = it.grid_size
            }
        })

        binding.deletedRecycler.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.deletedRecycler.adapter = NotesAdapter(requireContext(), emptyList()) {
            navigationController.editNote(findNavController(), it)
        }.apply { adapter = this }

        val keyProvider = MyItemKeyProvider(binding.deletedRecycler)

        val itemDetails = object: ItemDetailsLookup<Long>() {
            override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
                val view: View? = binding.deletedRecycler.findChildViewUnder(e.x, e.y)
                if (view != null) {
                    val holder: RecyclerView.ViewHolder = binding.deletedRecycler.getChildViewHolder(view)
                    if (holder is NoteViewHolder) {
                        return holder.getItemDetails(keyProvider.getKey(holder.pos))
                    }
                }
                return null
            }
        }

        val tracker = SelectionTracker.Builder(
            "deleted-selection",
            binding.deletedRecycler,
            keyProvider,
            itemDetails,
            StorageStrategy.createLongStorage()
        )
            .withSelectionPredicate(SelectionPredicates.createSelectAnything())
            .build()

        adapter.selectionTracker = tracker


        viewModel.deletedNotes.observe(viewLifecycleOwner, Observer {
            adapter.updateData(it)
        })
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}
