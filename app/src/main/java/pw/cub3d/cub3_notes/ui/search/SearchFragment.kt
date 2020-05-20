package pw.cub3d.cub3_notes.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_search.*
import pw.cub3d.cub3_notes.core.dagger.injector
import pw.cub3d.cub3_notes.databinding.FragmentSearchBinding
import pw.cub3d.cub3_notes.ui.NoteSelectionTrackerFactory
import pw.cub3d.cub3_notes.ui.bind
import pw.cub3d.cub3_notes.ui.home.NotesAdapter

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModels { injector.searchViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
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

        NoteSelectionTrackerFactory.buildTracker("search-selection", binding.searchResults).bind(adapter)
        val searchLabelsAdapter = SearchLabelsAdapter(requireContext()) {
            viewModel.searchQuery.value = it.title
        }
        binding.searchLabels.adapter = searchLabelsAdapter
        binding.searchLabels.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        viewModel.labels.observe(viewLifecycleOwner, Observer { searchLabelsAdapter.submitList(it) })

        viewModel.getSearchResults().observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }
}
