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
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_note_label_edit.*

import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.dao.LabelDao
import pw.cub3d.cub3_notes.databinding.FragmentNoteLabelEditBinding
import javax.inject.Inject

class NoteLabelEditFragment : Fragment() {

    @Inject lateinit var noteLabelEditViewModelFactory: NoteLabelEditViewModelFactory
    lateinit var noteLabelEditViewModel: NoteLabelEditViewModel
    @Inject lateinit var labelDao: LabelDao

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

        noteLabelEditViewModel.loadNote(arguments?.getLong(NoteLabelEditFragment.KEY_NOTE_ID, -1) ?: -1)

        noteLabelEditViewModel.labels.observe(viewLifecycleOwner, Observer {
            println("New labels: $it")

            noteLabelEdit_recycler.layoutManager = LinearLayoutManager(requireContext())
            noteLabelEdit_recycler.adapter = NoteLabelEditAdapter(requireContext(), it, labelDao, noteLabelEditViewModel.noteId.value!!)
        })
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    companion object {
        const val KEY_NOTE_ID = "note_id"
    }
}
