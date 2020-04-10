package pw.cub3d.cub3_notes.ui.search

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_search.*

import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.databinding.FragmentSearchBinding
import pw.cub3d.cub3_notes.ui.home.MyItemKeyProvider
import pw.cub3d.cub3_notes.ui.home.NoteViewHolder
import pw.cub3d.cub3_notes.ui.home.NotesAdapter
import javax.inject.Inject

class SearchFragment : Fragment() {

    @Inject lateinit var searchViewModelFactory: SearchViewModelFactory
    lateinit var viewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSearchBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(viewModelStore, searchViewModelFactory)
            .get(SearchViewModel::class.java)
        binding.query = viewModel.searchQuery

        return binding.root
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.searchQuery.observe(viewLifecycleOwner, Observer {
            viewModel.getSearchResults(it).observe(viewLifecycleOwner, Observer {
                search_results.layoutManager = LinearLayoutManager(requireContext())
                val adapter = NotesAdapter(requireContext(), it, {})
                search_results.adapter = adapter


                val keyProvider = MyItemKeyProvider(search_results)

                val itemDetails = object: ItemDetailsLookup<Long>() {
                    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
                        val view: View? = search_results.findChildViewUnder(e.x, e.y)
                        if (view != null) {
                            val holder: RecyclerView.ViewHolder = search_results.getChildViewHolder(view)
                            if (holder is NoteViewHolder) {
                                return holder.getItemDetails(keyProvider.getKey(holder.pos))
                            }
                        }
                        return null
                    }
                }

                val tracker = SelectionTracker.Builder(
                    "search-selection",
                    search_results,
                    keyProvider,
                    itemDetails,
                    StorageStrategy.createLongStorage()
                )
                    .withSelectionPredicate(SelectionPredicates.createSelectAnything())
                    .build()


                adapter.selectionTracker = tracker
            })
        })
    }

}
