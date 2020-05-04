package pw.cub3d.cub3_notes.ui.archived

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.archive_fragment.*
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.ui.home.MyItemKeyProvider
import pw.cub3d.cub3_notes.ui.home.NoteViewHolder
import pw.cub3d.cub3_notes.ui.home.NotesAdapter
import javax.inject.Inject

class ArchiveFragment : Fragment() {
    @Inject lateinit var viewModelFactory: ArchiveViewModelFactory
    private val viewModel: ArchiveViewModel by viewModels { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.archive_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        archive_recycler.layoutManager = GridLayoutManager(requireContext(), 2)
        archive_recycler.adapter = NotesAdapter(requireContext(), emptyList()) { note -> }

        val keyProvider = MyItemKeyProvider(archive_recycler)

        val itemDetails = object: ItemDetailsLookup<Long>() {
            override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
                val view: View? = archive_recycler.findChildViewUnder(e.x, e.y)
                if (view != null) {
                    val holder: RecyclerView.ViewHolder = archive_recycler.getChildViewHolder(view)
                    if (holder is NoteViewHolder) {
                        val id = holder.getItemDetails(keyProvider.getKey(holder.pos))
                        return id
                    }
                }
                return null
            }
        }

        val tracker = SelectionTracker.Builder(
            "archive-selection",
            archive_recycler,
            keyProvider,
            itemDetails,
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
