package pw.cub3d.cub3_notes.ui.home

import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Note.NOTE
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_home.*
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.RoomDB
import pw.cub3d.cub3_notes.database.entity.Note
import pw.cub3d.cub3_notes.ui.nav.NewNoteNavigationController
import javax.inject.Inject


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    @Inject lateinit var homeViewModelFactory: NotesViewModelFactory

    @Inject lateinit var newNoteNavigationController: NewNoteNavigationController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

            val adapter = NotesAdapter(requireContext(), it) { note -> newNoteNavigationController.editNote(findNavController(), note) }

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

            val adapter = NotesAdapter(requireContext(), it) { note -> newNoteNavigationController.editNote(findNavController(), note) }

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

        home_takeNote.setOnClickListener { newNoteNavigationController.navigateNewNote(findNavController()) }
        home_new_checkNote.setOnClickListener { newNoteNavigationController.navigateNewNote(findNavController(), Note.TYPE_CHECKBOX) }
        home_new_penNote.setOnClickListener { newNoteNavigationController.navigateNewNote(findNavController(), Note.TYPE_DRAW) }
        home_new_voiceNote.setOnClickListener{ newNoteNavigationController.navigateNewNote(findNavController(), Note.TYPE_AUDIO) }
        home_new_imgNote.setOnClickListener { newNoteNavigationController.navigateNewNote(findNavController(), Note.TYPE_IMAGE) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModelFactory = NotesViewModelFactory(RoomDB.getDatabase(requireContext()).notesDao())

        homeViewModel =
            ViewModelProviders.of(this, homeViewModelFactory).get(HomeViewModel::class.java)

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }
}