package pw.cub3d.cub3_notes.ui.noteLabels

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
import kotlinx.android.synthetic.main.fragment_note_label_edit.*
import pw.cub3d.cub3_notes.database.dao.LabelDao
import pw.cub3d.cub3_notes.databinding.FragmentNoteLabelEditBinding
import javax.inject.Inject

class NoteLabelEditFragment : Fragment() {

    @Inject lateinit var viewModelFactory: NoteLabelEditViewModelFactory
    private val viewModel: NoteLabelEditViewModel by viewModels { viewModelFactory }
    @Inject lateinit var labelDao: LabelDao

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = FragmentNoteLabelEditBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadNote(arguments?.getLong(KEY_NOTE_ID, -1) ?: -1)

        viewModel.labels.observe(viewLifecycleOwner, Observer {
            println("New labels: $it")

            noteLabelEdit_recycler.layoutManager = LinearLayoutManager(requireContext())
            noteLabelEdit_recycler.adapter = NoteLabelEditAdapter(requireContext(), it, labelDao, viewModel.noteId.value!!)
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
