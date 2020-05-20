package pw.cub3d.cub3_notes.ui.home

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.core.dagger.injector
import pw.cub3d.cub3_notes.core.database.entity.Note
import pw.cub3d.cub3_notes.core.database.repository.FilterType
import pw.cub3d.cub3_notes.core.database.repository.SortTypes
import pw.cub3d.cub3_notes.databinding.FragmentHomeBinding
import pw.cub3d.cub3_notes.ui.*
import pw.cub3d.cub3_notes.ui.dialog.addImage.AddImageDialog
import pw.cub3d.cub3_notes.ui.dialog.addVideo.AddVideoDialog

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels { injector.homeViewModelFactory() }

    lateinit var pinnedAdapter: NotesAdapter
    lateinit var otherAdapter: NotesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ToolbarController.setupToolbar(viewModel.settingsManager, this, binding.homeAppBar, binding.toolbar, "Search your notes")
        ToolbarController.setupSideNav(viewModel.settingsManager, this)

        binding.homePinnedNotes.layoutManager = NoteLayoutManager(viewLifecycleOwner, viewModel.settingsManager)
        binding.homeNotes.layoutManager = NoteLayoutManager(viewLifecycleOwner, viewModel.settingsManager)

        (requireActivity() as MainActivity).binding.navView.setNavigationItemSelectedListener {
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
            val menu = (requireActivity() as MainActivity).binding.navView.menu.findItem(R.id.hamburger_labels).subMenu
            menu.children.filter { it.groupId == R.id.hamburger_group_labels }.forEach { menu.removeItem(it.itemId) }

            it.forEach {
                menu.add(R.id.hamburger_group_labels, it.id.toInt(), Menu.FIRST, it.title).apply {
                    setIcon(R.drawable.ic_tag)
                    setOnMenuItemClickListener {
                        findNavController().navigate(R.id.nav_search, Bundle().apply {
                            putString("SEARCH_QUERY", "${it.title}")
                        })
                        true
                    }
                }
            }
        })

        val pinnedNoteItemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: ViewHolder,
                target: ViewHolder
            ): Boolean {
                val oldList = ArrayList(pinnedAdapter.items)
                val selectedItem = oldList.find { it.note.id == viewHolder.itemId }!!
                val targetIndex = oldList.indexOf(oldList.find { it.note.id == target.itemId })
                oldList.remove(selectedItem)
                oldList.add(targetIndex, selectedItem)

                oldList.forEach {
                    viewModel.upadateNotePosition(it, oldList.indexOf(it).toLong())
                }

                return true
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                viewModel.archiveNote(pinnedAdapter.items.find { it.note.id == viewHolder.itemId }!!)
                Toast.makeText(requireContext(), "Archived", Toast.LENGTH_LONG).show()
            }
        }
        val otherNoteItemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: ViewHolder,
                target: ViewHolder
            ): Boolean {
                val oldList = ArrayList(otherAdapter.items)
                val selectedItem = oldList.find { it.note.id == viewHolder.itemId }!!
                val targetIndex = oldList.indexOf(oldList.find { it.note.id == target.itemId })
                oldList.remove(selectedItem)
                oldList.add(targetIndex, selectedItem)

                oldList.forEach {
                    viewModel.upadateNotePosition(it, oldList.indexOf(it).toLong())
                }

                return true
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                viewModel.archiveNote(otherAdapter.items.find { it.note.id == viewHolder.itemId }!!)
                Toast.makeText(requireContext(), "Archived", Toast.LENGTH_LONG).show()
            }
        }
        ItemTouchHelper(pinnedNoteItemTouchHelperCallback).attachToRecyclerView(binding.homePinnedNotes)
        ItemTouchHelper(otherNoteItemTouchHelperCallback).attachToRecyclerView(binding.homeNotes)

        pinnedAdapter = NotesAdapter(requireContext()) { note, v -> viewModel.newNoteNavigationController.editNote(findNavController(), note, v) }
        binding.homePinnedNotes.adapter = pinnedAdapter

        NoteSelectionTrackerFactory.buildTracker("note-pin-selection", binding.homePinnedNotes).bind(pinnedAdapter)

        viewModel.pinned.observe(viewLifecycleOwner, Observer {
            binding.pinnedEmpty = it.isEmpty()

            if (it.isEmpty()) {
                binding.homePinnedNotes.visibility = View.GONE
                binding.homePinnedTitle.visibility = View.GONE
                binding.homeOthersTitle.visibility = View.GONE
            } else {
                binding.homePinnedNotes.visibility = View.VISIBLE
                binding.homePinnedTitle.visibility = View.VISIBLE
                binding.homeOthersTitle.visibility = View.VISIBLE
            }

            pinnedAdapter.submitList(it)
        })

        otherAdapter = NotesAdapter(requireContext()) { note, v -> viewModel.newNoteNavigationController.editNote(findNavController(), note, v) }
        binding.homeNotes.adapter = otherAdapter

        NoteSelectionTrackerFactory.buildTracker("note-selection", binding.homeNotes).bind(otherAdapter)

        viewModel.unpinned.observe(viewLifecycleOwner, Observer {
            binding.otherEmpty = it.isEmpty()

            otherAdapter.submitList(it)
        })

        binding.homeMoreToggle.setOnClickListener {
            BottomSheetBehavior.from(binding.homeMoreSheet).state = when (BottomSheetBehavior.from(binding.homeMoreSheet).state) {
                BottomSheetBehavior.STATE_HIDDEN -> BottomSheetBehavior.STATE_EXPANDED
                BottomSheetBehavior.STATE_EXPANDED -> BottomSheetBehavior.STATE_HIDDEN
                else -> BottomSheetBehavior.STATE_HIDDEN
            }
        }
        BottomSheetBehavior.from(binding.homeMoreSheet).state = BottomSheetBehavior.STATE_HIDDEN

        binding.homeMoreSettings.setOnClickListener { findNavController().navigate(R.id.nav_settings) }
        binding.homeMoreArchive.setOnClickListener { findNavController().navigate(R.id.nav_archive) }
        binding.homeMoreDeleted.setOnClickListener { findNavController().navigate(R.id.nav_deleted) }

        binding.homeSearch.setOnClickListener { findNavController().navigate(R.id.nav_search) }

        viewModel.settingsManager.quickNoteEnabled.observe(viewLifecycleOwner, Observer {
            val v = if (it) View.VISIBLE else View.GONE
            binding.homeNewCheckNote.visibility = v
            binding.homeNewImgNote.visibility = v
            binding.homeNewVideo.visibility = v
        })

        binding.homeTakeNote.setOnClickListener { viewModel.newNoteNavigationController.navigateNewNote(findNavController()) }
        binding.homeNewCheckNote.setOnClickListener { viewModel.newNoteNavigationController.navigateNewNote(findNavController(), Note.TYPE_CHECKBOX) }
//        home_new_penNote.setOnClickListener { newNoteNavigationController.navigateNewNote(findNavController(), Note.TYPE_DRAW) }
        binding.homeNewVoiceNote.setOnClickListener {
            Toast.makeText(requireContext(), "Hold to record", Toast.LENGTH_SHORT).show()
        }
        binding.homeNewVoiceNote.setOnLongClickListener {
            lifecycleScope.launch {
                viewModel.audioManager.startRecording(this@HomeFragment)
            }
            true
        }
        binding.homeNewVoiceNote.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                viewModel.audioManager.stopRecording()?.let {
                    viewModel.newNoteNavigationController.navigateNewNoteWithAudio(findNavController(), it.name)
                }
            }
            false
        }

        binding.homeNewVideo.setOnClickListener { AddVideoDialog(requireActivity(), viewModel.storageManager).show() }
        binding.homeNewImgNote.setOnClickListener { AddImageDialog(requireActivity(), viewModel.storageManager).show() }

        BottomSheetBehavior.from(binding.homeFilterSheet).state = BottomSheetBehavior.STATE_HIDDEN
        binding.homeFilter.setOnClickListener { BottomSheetBehavior.from(binding.homeFilterSheet).state = if (BottomSheetBehavior.from(binding.homeFilterSheet).state == BottomSheetBehavior.STATE_HIDDEN) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_HIDDEN }
        binding.homeFilterReminder.setOnClickListener { viewModel.filter.postValue(FilterType.REMINDERS) }
        binding.homeFilterAudio.setOnClickListener { viewModel.filter.postValue(FilterType.AUDIO) }
        binding.homeFilterCheck.setOnClickListener { viewModel.filter.postValue(FilterType.CHECKBOX) }
        binding.homeFilterImage.setOnClickListener { viewModel.filter.postValue(FilterType.IMAGE) }
        binding.homeFilterVideo.setOnClickListener { viewModel.filter.postValue(FilterType.VIDEO) }
        binding.homeFilterTag.setOnClickListener { viewModel.filter.postValue(FilterType.TAGGED) }
        binding.homeFilterAll.setOnClickListener { viewModel.filter.postValue(FilterType.ALL) }

        BottomSheetBehavior.from(binding.homeSortSheet).state = BottomSheetBehavior.STATE_HIDDEN
        binding.homeSort.setOnClickListener { BottomSheetBehavior.from(binding.homeSortSheet).state = if (BottomSheetBehavior.from(binding.homeSortSheet).state == BottomSheetBehavior.STATE_HIDDEN) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_HIDDEN }

        binding.homeSortManual.setOnClickListener { viewModel.sort.postValue(SortTypes.MANUAL) }
        binding.homeSortCreatedAsc.setOnClickListener { viewModel.sort.postValue(SortTypes.CREATED_ASC) }
        binding.homeSortCreatedDsc.setOnClickListener { viewModel.sort.postValue(SortTypes.CREATED_DSC) }
        binding.homeSortChangeAsc.setOnClickListener { viewModel.sort.postValue(SortTypes.MODIFY_ASC) }
        binding.homeSortChangeDsc.setOnClickListener { viewModel.sort.postValue(SortTypes.MODIFY_DSC) }
        binding.homeSortViewAsc.setOnClickListener { viewModel.sort.postValue(SortTypes.VIEW_ASC) }
        binding.homeSortViewDsc.setOnClickListener { viewModel.sort.postValue(SortTypes.VIEW_DSC) }
        binding.homeSortAlpha.setOnClickListener { viewModel.sort.postValue(SortTypes.TITLE_ALPHABETICAL) }
        binding.homeSortAlphaReverse.setOnClickListener { viewModel.sort.postValue(SortTypes.TITLE_REVERSE_ALPHABETICAL) }

        binding.homeLabels.setOnClickListener { findNavController().navigate(R.id.nav_label_edit) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentHomeBinding.inflate(inflater, container, false).apply {
        binding = this
        pinnedEmpty = false
        otherEmpty = false
    }.root
}
