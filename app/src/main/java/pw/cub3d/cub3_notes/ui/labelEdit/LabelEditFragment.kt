package pw.cub3d.cub3_notes.ui.labelEdit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import kotlinx.android.synthetic.main.fragment_label_edit.*
import pw.cub3d.cub3_notes.core.dagger.injector
import pw.cub3d.cub3_notes.databinding.FragmentLabelEditBinding

class LabelEditFragment : Fragment() {
    private lateinit var binding: FragmentLabelEditBinding
    private val labelEditViewModel: LabelEditViewModel by viewModels { injector.labelEditViewModelFactory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLabelEditBinding.inflate(layoutInflater, container, false)
        binding.viewModel = labelEditViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editLabelConfirm.setOnClickListener {
            val cp = ColorPicker(requireActivity(), 200, 200, 0)
            cp.show()
            cp.enableAutoClose()
            cp.setCallback { color ->
                labelEditViewModel.saveNewLabel("#${Integer.toHexString(color)}")
            }
            cp.setOnDismissListener {
                println("Dismiss")
                labelEditViewModel.saveNewLabel(null)
            }
        }

        binding.editLabelRecycler.layoutManager = LinearLayoutManager(requireContext())
        val labelAdapter = LabelAdapter(requireContext())
        binding.editLabelRecycler.adapter = labelAdapter
        labelEditViewModel.labels.observe(viewLifecycleOwner, Observer { labelAdapter.submitList(it) })

        binding.editLabelNewText.requestFocus()
    }

}
