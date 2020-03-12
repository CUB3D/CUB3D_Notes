package pw.cub3d.cub3_notes.ui.home

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.activity.MainActivity
import pw.cub3d.cub3_notes.database.entity.Note
import pw.cub3d.cub3_notes.ui.dialog.addImage.AddImageDialog
import pw.cub3d.cub3_notes.ui.nav.NewNoteNavigationController
import javax.inject.Inject


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    @Inject lateinit var homeViewModelFactory: NotesViewModelFactory
    @Inject lateinit var newNoteNavigationController: NewNoteNavigationController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).setupActionBarWithNavController(findNavController(), (requireActivity() as MainActivity).appBarConfiguration)
        requireActivity().nav_view.setupWithNavController(findNavController())

        requireActivity().nav_view.setNavigationItemSelectedListener {
            println("Nav item selected: $it")
            if (it.itemId == R.id.nav_new_label) {
                findNavController().navigate(R.id.nav_label_edit)
            }
            if(it.itemId == R.id.sidenav_settings) {
                findNavController().navigate(R.id.nav_settings)
            }

            true
        }

        homeViewModel.labels.observe(viewLifecycleOwner, Observer {
            val menu = requireActivity().nav_view.menu.findItem(R.id.hamburger_labels).subMenu

            it.forEach {
                menu.add(R.id.hamburger_group_labels, it.id.toInt(), Menu.FIRST, it.title).apply {
                    setIcon(R.drawable.ic_tag)
                }
            }
        })

        homeViewModel.pinnedNotes.observe(viewLifecycleOwner, Observer {
            if(it.isEmpty()) {
                home_pinnedNotes.visibility = View.GONE
                home_pinnedTitle.visibility = View.GONE
                home_othersTitle.visibility = View.GONE
                return@Observer
            }

            home_pinnedNotes.visibility = View.VISIBLE
            home_pinnedTitle.visibility = View.VISIBLE
            home_othersTitle.visibility = View.VISIBLE

            home_pinnedNotes.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

            val adapter = NotesAdapter(requireContext(), it) { note -> newNoteNavigationController.editNote(findNavController(), note) }

            home_pinnedNotes.adapter = adapter

            val keyProvider = MyItemKeyProvider(home_notes)

            val itemDetails = object: ItemDetailsLookup<Long>() {
                override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
                    val view: View? = home_notes.findChildViewUnder(e.x, e.y)
                    if (view != null) {
                        val holder: ViewHolder = home_notes.getChildViewHolder(view)
                        if (holder is NoteViewHolder) {
                            return holder.getItemDetails(keyProvider.getKey(holder.pos))
                        }
                    }
                    return null
                }
            }

            val tracker = SelectionTracker.Builder(
                "notes-pin-selection",
                home_pinnedNotes,
                keyProvider,
                itemDetails,
                StorageStrategy.createLongStorage()
            )
                .withSelectionPredicate(SelectionPredicates.createSelectAnything())
                .build()

            adapter.selectionTracker = tracker
        })

        homeViewModel.unpinnedNotes.observe(viewLifecycleOwner, Observer {
            home_notes.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

            val adapter = NotesAdapter(requireContext(), it) { note -> newNoteNavigationController.editNote(findNavController(), note) }

            home_notes.adapter = adapter

            val keyProvider = MyItemKeyProvider(home_notes)

            val itemDetails = object: ItemDetailsLookup<Long>() {
                override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
                    val view: View? = home_notes.findChildViewUnder(e.x, e.y)
                    if (view != null) {
                        val holder: ViewHolder = home_notes.getChildViewHolder(view)
                        if (holder is NoteViewHolder) {
                            val id = holder.getItemDetails(keyProvider.getKey(holder.pos))
                            return id
                        }
                    }
                    return null
                }
            }

            val tracker = SelectionTracker.Builder(
                "notes-selection",
                home_notes,
                keyProvider,
                itemDetails,
                StorageStrategy.createLongStorage()
            )
                .withSelectionPredicate(SelectionPredicates.createSelectAnything())
                .build()

            adapter.selectionTracker = tracker

            tracker.addObserver(object: SelectionTracker.SelectionObserver<Long>() {
                override fun onItemStateChanged(key: Long, selected: Boolean) {
                    println("Item state change: $key, $selected")
                    home_notes.findViewHolderForAdapterPosition(keyProvider.getPosition(key))?.let {
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
        })

        home_takeNote.setOnClickListener { newNoteNavigationController.navigateNewNote(findNavController()) }
        home_new_checkNote.setOnClickListener { newNoteNavigationController.navigateNewNote(findNavController(), Note.TYPE_CHECKBOX) }
        home_new_penNote.setOnClickListener { newNoteNavigationController.navigateNewNote(findNavController(), Note.TYPE_DRAW) }
        home_new_voiceNote.setOnClickListener{ newNoteNavigationController.navigateNewNote(findNavController(), Note.TYPE_AUDIO) }
        home_new_imgNote.setOnClickListener {
            AddImageDialog(requireActivity()).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        homeViewModel = ViewModelProvider(viewModelStore, homeViewModelFactory)
            .get(HomeViewModel::class.java)

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}