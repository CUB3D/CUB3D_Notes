package pw.cub3d.cub3_notes.ui.noteLabels

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.AndroidSupportInjection

import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.databinding.FragmentNoteLabelEditBinding
import javax.inject.Inject

class NoteLabelEditFragment : Fragment() {

    @Inject lateinit var noteLabelEditViewModelFactory: NoteLabelEditViewModelFactory
    lateinit var noteLabelEditViewModel: NoteLabelEditViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        noteLabelEditViewModel = ViewModelProvider(viewModelStore, noteLabelEditViewModelFactory)
            .get(NoteLabelEditViewModel::class.java)

        val binding: FragmentNoteLabelEditBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_note_label_edit, container, false)
        binding.viewModel = noteLabelEditViewModel
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteLabelEditViewModel.labels.observe(viewLifecycleOwner, Observer {
            println("New labels: $it")
        })
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

}
