package pw.cub3d.cub3_notes.ui.labelEdit

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_label_edit.*
import pw.cub3d.cub3_notes.databinding.FragmentLabelEditBinding
import javax.inject.Inject

class LabelEditFragment : Fragment() {
    @Inject lateinit var labelEditViewModelFactory: LabelEditViewModelFactory
    private val labelEditViewModel: LabelEditViewModel by viewModels { labelEditViewModelFactory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentLabelEditBinding.inflate(layoutInflater, container, false)
        binding.viewModel = labelEditViewModel
        return binding.root
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
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
