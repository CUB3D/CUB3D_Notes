package pw.cub3d.cub3_notes.ui.labelEdit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_label_edit.*
import pw.cub3d.cub3_notes.core.dagger.injector
import pw.cub3d.cub3_notes.databinding.FragmentLabelEditBinding

class LabelEditFragment : Fragment() {
    private val labelEditViewModel: LabelEditViewModel by viewModels { injector.labelEditViewModelFactory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentLabelEditBinding.inflate(layoutInflater, container, false)
        binding.viewModel = labelEditViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editLabel_confirm.setOnClickListener {
            labelEditViewModel.saveNewLabel()
        }

        labelEditViewModel.labels.observe(viewLifecycleOwner, Observer {
            editLabel_recycler.layoutManager = LinearLayoutManager(requireContext())
            editLabel_recycler.adapter = LabelAdapter(requireContext(), it)
        })
    }

}
