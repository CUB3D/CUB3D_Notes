package pw.cub3d.cub3_notes.ui.home

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.SettingsManager
import pw.cub3d.cub3_notes.StorageManager
import pw.cub3d.cub3_notes.activity.MainActivity
import pw.cub3d.cub3_notes.database.entity.Note
import pw.cub3d.cub3_notes.ui.dialog.addImage.AddImageDialog
import pw.cub3d.cub3_notes.ui.nav.NewNoteNavigationController
import javax.inject.Inject
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    @Inject lateinit var homeViewModelFactory: NotesViewModelFactory
    private val homeViewModel: HomeViewModel by viewModels { homeViewModelFactory }

    @Inject lateinit var newNoteNavigationController: NewNoteNavigationController
    @Inject lateinit var storageManager: StorageManager
    @Inject lateinit var settingsManager: SettingsManager

    lateinit var pinnedAdapter: NotesAdapter
    lateinit var otherAdapter: NotesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).setupActionBarWithNavController(findNavController(), (requireActivity() as MainActivity).appBarConfiguration)
        requireActivity().nav_view.setupWithNavController(findNavController())
        (requireActivity() as AppCompatActivity).supportActionBar!!.title = "Search your notes"
        (requireActivity() as AppCompatActivity).toolbar.setOnClickListener { findNavController().navigate(R.id.nav_search) }
        requireActivity().nav_view.menu.getItem(0).isChecked = true;


        home_pinnedNotes.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        home_notes.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        settingsManager.noteLayout.observe(viewLifecycleOwner, Observer {
            println("Layout changed to: $it")

            (home_pinnedNotes.layoutManager as StaggeredGridLayoutManager).apply {
                spanCount = it.grid_size
            }

            (home_notes.layoutManager as StaggeredGridLayoutManager).apply {
                spanCount = it.grid_size
            }
        })

        requireActivity().nav_view.setNavigationItemSelectedListener {
            println("Nav item selected: $it")
            when (it.itemId) {
                R.id.nav_new_label -> findNavController().navigate(R.id.nav_label_edit)
                R.id.sidenav_settings -> findNavController().navigate(R.id.nav_settings)
                R.id.sidenav_archived -> findNavController().navigate(R.id.nav_archive)
                R.id.sidenav_deleted -> findNavController().navigate(R.id.nav_deleted)
                R.id.sidenav_reminders -> homeViewModel.setShowOnlyReminders(true)
                R.id.sidenav_notes -> homeViewModel.setShowOnlyReminders(false)
            }

            true
        }

        homeViewModel.labels.observe(viewLifecycleOwner, Observer {
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
                    homeViewModel.upadateNotePosition(it, oldList.indexOf(it).toLong())
                }

                pinnedAdapter.updateData(oldList)

                return true
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                homeViewModel.archiveNote(pinnedAdapter.notes.find { it.note.id ==  viewHolder.itemId}!!)
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
                    homeViewModel.upadateNotePosition(it, oldList.indexOf(it).toLong())
                }

                otherAdapter.updateData(oldList)

                return true
            }

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                homeViewModel.archiveNote(otherAdapter.notes.find { it.note.id ==  viewHolder.itemId}!!)
                Toast.makeText(requireContext(), "Archived", Toast.LENGTH_LONG).show()
            }
        }
        ItemTouchHelper(pinnedNoteItemTouchHelperCallback).attachToRecyclerView(home_pinnedNotes)
        ItemTouchHelper(otherNoteItemTouchHelperCallback).attachToRecyclerView(home_notes)


        pinnedAdapter = NotesAdapter(requireContext(), emptyList()) { note -> newNoteNavigationController.editNote(findNavController(), note) }
        home_pinnedNotes.adapter = pinnedAdapter
        val keyProvider = MyItemKeyProvider(home_pinnedNotes)

        val tracker = SelectionTracker.Builder(
            "notes-pin-selection",
            home_pinnedNotes,
            keyProvider,
            ItemDetailsProvider(home_pinnedNotes, keyProvider),
            StorageStrategy.createLongStorage()
        )
            .withSelectionPredicate(SelectionPredicates.createSelectAnything())
            .build()

        pinnedAdapter.selectionTracker = tracker


        homeViewModel.pinnedNotes.observe(viewLifecycleOwner, Observer {
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

        otherAdapter = NotesAdapter(requireContext(), emptyList()) { note -> newNoteNavigationController.editNote(findNavController(), note) }
        home_notes.adapter = otherAdapter

        val otherKeyProvider = MyItemKeyProvider(home_notes)

        val otherTracker = SelectionTracker.Builder(
            "notes-selection",
            home_notes,
            otherKeyProvider,
            ItemDetailsProvider(home_notes, otherKeyProvider),
            StorageStrategy.createLongStorage()
        )
            .withSelectionPredicate(SelectionPredicates.createSelectAnything())
            .build()

        otherTracker.addObserver(object: SelectionTracker.SelectionObserver<Long>() {
            override fun onItemStateChanged(key: Long, selected: Boolean) {
                println("Item state change: $key, $selected")
                home_notes.findViewHolderForAdapterPosition(otherKeyProvider.getPosition(key))?.let {
                    (it as NoteViewHolder).onSelected()
                }
            }

            override fun onSelectionChanged() {
            }

            override fun onSelectionRestored() {
                println("Selection restored")
            }

            override fun onSelectionRefresh() {
                println("Selection refreshed")
            }
        })

        otherAdapter.selectionTracker = otherTracker

        homeViewModel.unpinnedNotes.observe(viewLifecycleOwner, Observer {
            otherAdapter.updateData(it)
        })

        home_takeNote.setOnClickListener { newNoteNavigationController.navigateNewNote(findNavController()) }
        home_new_checkNote.setOnClickListener { newNoteNavigationController.navigateNewNote(findNavController(), Note.TYPE_CHECKBOX) }
//        home_new_penNote.setOnClickListener { newNoteNavigationController.navigateNewNote(findNavController(), Note.TYPE_DRAW) }
//        home_new_voiceNote.setOnClickListener{ newNoteNavigationController.navigateNewNote(findNavController(), Note.TYPE_AUDIO) }
        home_new_imgNote.setOnClickListener {
            AddImageDialog(requireActivity(), storageManager).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}

interface ProvidesItemDetails {
    fun getItemDetails(key: Long): ItemDetailsLookup.ItemDetails<Long>
    fun getItemPosition(): Int
}

class ItemDetailsProvider(private val recycler: RecyclerView, private val keyProvider: ItemKeyProvider<Long>) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        recycler.findChildViewUnder(e.x, e.y)?.let { v ->
            val holder: ViewHolder = recycler.getChildViewHolder(v)
            if (holder is ProvidesItemDetails) {
                return holder.getItemDetails(keyProvider.getKey(holder.getItemPosition())!!)
            }
        }
        return null
    }
}