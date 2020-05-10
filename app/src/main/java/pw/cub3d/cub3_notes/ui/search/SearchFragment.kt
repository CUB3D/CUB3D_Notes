package pw.cub3d.cub3_notes.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_search.*
import pw.cub3d.cub3_notes.core.dagger.injector
import pw.cub3d.cub3_notes.databinding.FragmentSearchBinding
import pw.cub3d.cub3_notes.ui.home.ItemDetailsProvider
import pw.cub3d.cub3_notes.ui.home.NotesAdapter

class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels { injector.searchViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.query = viewModel.searchQuery
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            it.getString("SEARCH_QUERY")?.let {
                viewModel.searchQuery.value = it
            }
        }

        search_results.layoutManager = LinearLayoutManager(requireContext())
        val adapter = NotesAdapter(requireContext()) { note, v -> viewModel.newNoteNavigationController.editNote(findNavController(), note, v) }
        search_results.adapter = adapter

        val itemDetailsProvider = ItemDetailsProvider(search_results)

        val tracker = SelectionTracker.Builder(
            "search-selection",
            search_results,
            itemDetailsProvider.keyProvider,
            itemDetailsProvider,
            StorageStrategy.createLongStorage()
        )
            .withSelectionPredicate(SelectionPredicates.createSelectAnything())
            .build()


        adapter.selectionTracker = tracker

        viewModel.searchQuery.observe(viewLifecycleOwner, Observer {
            viewModel.getSearchResults(it).observe(viewLifecycleOwner, Observer {
                adapter.updateData(it)
            })
        })
    }
}
