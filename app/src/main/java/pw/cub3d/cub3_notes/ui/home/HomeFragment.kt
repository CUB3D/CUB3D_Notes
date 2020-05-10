package pw.cub3d.cub3_notes.ui.home

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.core.dagger.injector
import pw.cub3d.cub3_notes.core.database.entity.Note
import pw.cub3d.cub3_notes.ui.*
import pw.cub3d.cub3_notes.ui.dialog.addImage.AddImageDialog
import pw.cub3d.cub3_notes.ui.dialog.addVideo.AddVideoDialog


class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels { injector.homeViewModelFactory() }

    lateinit var pinnedAdapter: NotesAdapter
    lateinit var otherAdapter: NotesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ToolbarController.setupToolbar(viewModel.settingsManager, this, home_appBar, toolbar, "Search your notes")
        ToolbarController.setupSideNav(viewModel.settingsManager, this)

        home_pinnedNotes.layoutManager = NoteLayoutManager(viewLifecycleOwner, viewModel.settingsManager)
        home_notes.layoutManager = NoteLayoutManager(viewLifecycleOwner, viewModel.settingsManager)

        requireActivity().nav_view.setNavigationItemSelectedListener {
            println("Nav item selected: $it")
            when (it.itemId) {
                R.id.nav_new_label -> findNavController().navigate(R.id.nav_label_edit)
                R.id.sidenav_settings -> findNavController().navigate(R.id.nav_settings)
                R.id.sidenav_archived -> findNavController().navigate(R.id.nav_archive)
                R.id.sidenav_deleted -> findNavController().navigate(R.id.nav_deleted)
                R.id.sidenav_reminders -> viewModel.setShowOnlyReminders(true)
                R.id.sidenav_notes -> viewModel.setShowOnlyReminders(false)
            }

            true
        }

        viewModel.labels.observe(viewLifecycleOwner, Observer {
            val menu = requireActivity().nav_view.menu.findItem(R.id.hamburger_labels).subMenu
            menu.children.filter { it.groupId == R.id.hamburger_group_labels }.forEach { menu.removeItem(it.itemId) }

            it.forEach {
                menu.add(R.id.hamburger_group_labels, it.id.toInt(), Menu.FIRST, it.title).apply {
                    setIcon(R.drawable.ic_tag)
                    setOnMenuItemClickListener {
                        findNavController().navigate(R.id.nav_search, Bundle().apply {
                            putString("SEARCH_QUERY", "tag:${it.title}")
                        })
                        true
                    }
                }
            }
        })

        val pinnedNoteItemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: ViewHolder,
                target: ViewHolder
            ): Boolean {
                val oldList = ArrayList(pinnedAdapter.notes)
                val selectedItem = oldList.find { it.note.id == viewHolder.itemId }!!
                val targetIndex = oldList.indexOf(oldList.find { it.note.id == target.itemId })
                oldList.remove(selectedItem)
                oldList.add(targetIndex, selectedItem)

                oldList.forEach {
                    viewModel.upadateNotePosition(it, oldList.indexOf(it).toLong())
                }

                pinnedAdapter.updateData(oldList)

                return true
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                viewModel.archiveNote(pinnedAdapter.notes.find { it.note.id ==  viewHolder.itemId}!!)
                Toast.makeText(requireContext(), "Archived", Toast.LENGTH_LONG).show()
            }
        }
        val otherNoteItemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: ViewHolder,
                target: ViewHolder
            ): Boolean {
                val oldList = ArrayList(otherAdapter.notes)
                val selectedItem = oldList.find { it.note.id == viewHolder.itemId }!!
                val targetIndex = oldList.indexOf(oldList.find { it.note.id == target.itemId })
                oldList.remove(selectedItem)
                oldList.add(targetIndex, selectedItem)

                oldList.forEach {
                    viewModel.upadateNotePosition(it, oldList.indexOf(it).toLong())
                }

                otherAdapter.updateData(oldList)

                return true
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                viewModel.archiveNote(otherAdapter.notes.find { it.note.id ==  viewHolder.itemId}!!)
                Toast.makeText(requireContext(), "Archived", Toast.LENGTH_LONG).show()
            }
        }
        ItemTouchHelper(pinnedNoteItemTouchHelperCallback).attachToRecyclerView(home_pinnedNotes)
        ItemTouchHelper(otherNoteItemTouchHelperCallback).attachToRecyclerView(home_notes)

        pinnedAdapter = NotesAdapter(requireContext()) { note, v -> viewModel.newNoteNavigationController.editNote(findNavController(), note, v) }
        home_pinnedNotes.adapter = pinnedAdapter

        NoteSelectionTrackerFactory.buildTracker("note-pin-selection", home_pinnedNotes).bind(pinnedAdapter)

        viewModel.pinnedNotes.observe(viewLifecycleOwner, Observer {
            if(it.isEmpty()) {
                home_pinnedNotes.visibility = View.GONE
                home_pinnedTitle.visibility = View.GONE
                home_othersTitle.visibility = View.GONE
            } else {
                home_pinnedNotes.visibility = View.VISIBLE
                home_pinnedTitle.visibility = View.VISIBLE
                home_othersTitle.visibility = View.VISIBLE
            }
            pinnedAdapter.updateData(it)
        })

        otherAdapter = NotesAdapter(requireContext()) { note, v -> viewModel.newNoteNavigationController.editNote(findNavController(), note, v) }
        home_notes.adapter = otherAdapter

        NoteSelectionTrackerFactory.buildTracker("note-selection", home_notes).bind(otherAdapter)

        viewModel.unpinnedNotes.observe(viewLifecycleOwner, Observer {
            otherAdapter.updateData(it)
        })

        home_more_toggle.setOnClickListener {
            BottomSheetBehavior.from(home_more_sheet).state = when (BottomSheetBehavior.from(home_more_sheet).state) {
                BottomSheetBehavior.STATE_COLLAPSED -> BottomSheetBehavior.STATE_EXPANDED
                BottomSheetBehavior.STATE_EXPANDED -> BottomSheetBehavior.STATE_COLLAPSED
                else -> BottomSheetBehavior.STATE_COLLAPSED
            }
        }
        BottomSheetBehavior.from(home_more_sheet).state = BottomSheetBehavior.STATE_COLLAPSED

        home_more_settings.setOnClickListener { findNavController().navigate(R.id.nav_settings) }
        home_more_archive.setOnClickListener { findNavController().navigate(R.id.nav_archive) }
        home_more_deleted.setOnClickListener { findNavController().navigate(R.id.nav_deleted) }

        home_search.setOnClickListener { findNavController().navigate(R.id.nav_search) }


        home_takeNote.setOnClickListener { viewModel.newNoteNavigationController.navigateNewNote(findNavController()) }
        home_new_checkNote.setOnClickListener { viewModel.newNoteNavigationController.navigateNewNote(findNavController(), Note.TYPE_CHECKBOX) }
//        home_new_penNote.setOnClickListener { newNoteNavigationController.navigateNewNote(findNavController(), Note.TYPE_DRAW) }
        home_new_voiceNote.setOnLongClickListener {
            viewModel.audioManager.startRecording()
            true
        }
        home_new_voiceNote.setOnTouchListener { v, event ->
            if(event.action == MotionEvent.ACTION_UP) {
                viewModel.audioManager.stopRecording()?.let {
                    viewModel.newNoteNavigationController.navigateNewNoteWithAudio(findNavController(), it.name)
                }
            }
            false
        }
        home_new_video.setOnClickListener {
            AddVideoDialog(requireActivity(), viewModel.storageManager).show()
        }
        home_new_imgNote.setOnClickListener {
            AddImageDialog(requireActivity(), viewModel.storageManager).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}