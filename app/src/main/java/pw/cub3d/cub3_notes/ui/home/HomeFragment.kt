package pw.cub3d.cub3_notes.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.RoomDB


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val factory = NotesViewModelFactory(RoomDB.getDatabase(requireContext()).notesDao())

        homeViewModel =
            ViewModelProviders.of(this, factory).get(HomeViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        homeViewModel.pinnedNotes.observe(this, Observer {
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
            val adapter = NotesAdapter(requireContext(), it) {
                // On note clicked
                findNavController(this).navigate(R.id.nav_new_note, Bundle().apply {
                    putParcelable("NOTE_ID", it)
                })
            }

            home_pinnedNotes.adapter = adapter

            val keyProvider = StableIdKeyProvider(home_notes)

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

        homeViewModel.unpinnedNotes.observe(this, Observer {
            home_notes.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

            val adapter =  NotesAdapter(requireContext(), it) {
                // On note clicked
                findNavController(this).navigate(R.id.nav_new_note, Bundle().apply {
                    putParcelable("NOTE_ID", it)
                })
            }

            home_notes.adapter = adapter

            val keyProvider = StableIdKeyProvider(home_notes)

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

        return root
    }
}