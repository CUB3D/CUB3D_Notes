package pw.cub3d.cub3_notes.ui.newnote

import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_new_note.*
import kotlinx.coroutines.runBlocking
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.core.dagger.injector
import pw.cub3d.cub3_notes.core.database.entity.Note
import pw.cub3d.cub3_notes.core.utils.distinctUntilChangedBy
import pw.cub3d.cub3_notes.core.utils.ignoreFirstValue
import pw.cub3d.cub3_notes.databinding.FragmentNewNoteBinding
import pw.cub3d.cub3_notes.ui.dialog.addVideo.AddVideoDialog
import pw.cub3d.cub3_notes.ui.dialog.reminderdialog.ReminderDialog
import pw.cub3d.cub3_notes.ui.nav.NewNoteNavigationController
import pw.cub3d.cub3_notes.ui.noteLabels.NoteLabelEditFragment
import java.io.File


class NewNoteFragment : Fragment() {

    private val viewModel: NewNoteViewModel by viewModels { injector.newNoteViewModelFactory() }

    private lateinit var binding: FragmentNewNoteBinding

    private lateinit var checkBoxAdapter: CheckBoxAdapter
    private lateinit var audioAdapter: AudioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        arguments?.let {
            it.getBoolean(NewNoteNavigationController.KEY_NEW_NOTE, false).takeIf { it }?.let { runBlocking { viewModel.newNote().await() } }
            it.getLong(NewNoteNavigationController.KEY_NOTE, -1).takeIf { it != -1L }?.let { runBlocking { viewModel.loadNote(it).await() } }
            it.getString(NewNoteNavigationController.KEY_NOTE_TYPE)?.let { type -> viewModel.setNoteType(type) }
            it.getString(NewNoteNavigationController.KEY_NOTE_IMAGE_PATH)?.let { imagePath -> viewModel.addImage(imagePath) }
            it.getString(NewNoteNavigationController.KEY_NOTE_AUDIO_PATH)?.let { path -> viewModel.addAudioClip(path) }
            it.getString(NewNoteNavigationController.KEY_NOTE_VIDEO_PATH)?.let { path -> viewModel.addVideo(path) }
        }


        binding = FragmentNewNoteBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        requireContext().obtainStyledAttributes(intArrayOf(R.attr.colorSurface)).apply {
            binding.noteColour = this.getColor(0, Color.WHITE)
        }.recycle()
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.type.observe(viewLifecycleOwner, Observer { type ->
            if(type == Note.TYPE_CHECKBOX) {
                createNote_text.visibility = View.GONE
                createNote_checkBoxes.visibility = View.VISIBLE
                createNote_newItem.visibility = View.VISIBLE
            } else if(type == Note.TYPE_TEXT) {
                createNote_text.visibility = View.VISIBLE
                createNote_checkBoxes.visibility = View.GONE
                createNote_newItem.visibility = View.GONE
            }
        })

        viewModel.modificationTime.observe(viewLifecycleOwner, Observer {
            createNote_lastEdited.text = it
        })

        viewModel.videos.observe(viewLifecycleOwner, Observer {
            it.firstOrNull()?.let { v ->

                val player = SimpleExoPlayer.Builder(requireContext()).build()

                val dataSourceFactory = DefaultDataSourceFactory(
                    requireContext(),
                    Util.getUserAgent(requireContext(), "Notes")
                )
                val videoSource =
                    ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                        File(viewModel.storageManager.getVideoDir(), v.fileName).toUri()
                    )

                binding.createNoteVideo.player = player
                player.prepare(videoSource)
                //TODO: release the player

                binding.createNoteVideo.visibility = View.VISIBLE
            }
        })

        binding.createNoteAudio.layoutManager = LinearLayoutManager(requireContext())
        audioAdapter = AudioAdapter(requireContext())
        binding.createNoteAudio.adapter = audioAdapter
        viewModel.audioClips.observe(viewLifecycleOwner, Observer {
            audioAdapter.updateData(it)
        })


        createNote_checkBoxes.layoutManager = LinearLayoutManager(requireContext())
        checkBoxAdapter = CheckBoxAdapter(requireContext(), emptyList(), viewModel)
        createNote_checkBoxes.adapter = checkBoxAdapter
        val callback: ItemTouchHelper.Callback = object: ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                println("Get chkbox move flags")

                val selectedItem =
                    checkBoxAdapter.checkboxEntries.find { it.id == viewHolder.itemId }!!

                return makeMovementFlags(
                    ItemTouchHelper.UP.or(ItemTouchHelper.DOWN),
                    if (selectedItem.checked) ItemTouchHelper.RIGHT else ItemTouchHelper.LEFT
                )
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                println("On chkbox move")
                val oldList = ArrayList(checkBoxAdapter.checkboxEntries)
                val selectedItem = oldList.find { it.id == viewHolder.itemId }!!
                val targetIndex = oldList.indexOf(oldList.find { it.id == target.itemId })
                oldList.remove(selectedItem)
                oldList.add(targetIndex, selectedItem)

                oldList.forEach {
                    viewModel.upadateCheckboxPosition(it, oldList.indexOf(it))
                }

                (createNote_checkBoxes.adapter as CheckBoxAdapter).notifyDataSetChanged()

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                println("on chkbox swip")
                if (direction == ItemTouchHelper.LEFT) {
                    viewModel.onCheckboxChecked(
                        checkBoxAdapter.checkboxEntries[viewHolder.adapterPosition],
                        true
                    )
                    //TODO: is this the correct pos
                    (createNote_checkBoxes.adapter as CheckBoxAdapter).notifyDataSetChanged()
                }

                if (direction == ItemTouchHelper.RIGHT) {
                    viewModel.onCheckboxChecked(
                        checkBoxAdapter.checkboxEntries[viewHolder.adapterPosition],
                        false
                    )
                    //TODO: is this the correct pos
                    (createNote_checkBoxes.adapter as CheckBoxAdapter).notifyDataSetChanged()
                }
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(createNote_checkBoxes)

        //TODO sort in room junction
        viewModel.checkboxes.map { it.sortedBy { it.position } }.distinctUntilChangedBy { old, new -> old.map { it.id } == new.map { it.id } }.observe(viewLifecycleOwner, Observer { checkboxes ->
            println("Updating checkboxes: $checkboxes")
            checkBoxAdapter.updateData(checkboxes)
        })

        checkBoxAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                val vh = binding.createNoteCheckBoxes.findViewHolderForAdapterPosition((binding.createNoteCheckBoxes.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()) as CheckBoxViewHolder
                vh.view.checkboxEntryText.requestFocus()
            }
        })

        viewModel.defaultNoteColours.observe(viewLifecycleOwner, Observer {
            createNote_more_colors.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            createNote_more_colors.adapter = ColoursAdapter(this, it, viewModel)
        })

        viewModel.images.observe(viewLifecycleOwner, Observer {
            it.firstOrNull()?.let {
                Glide.with(this@NewNoteFragment)
                    .load(it.getFile(requireContext()))
                    .into(createNote_image)
            }
        })

        viewModel.title.distinctUntilChanged().ignoreFirstValue().observe(viewLifecycleOwner, Observer {
            viewModel.onTitleChange(it)
        })

        viewModel.text.distinctUntilChanged().ignoreFirstValue().observe(viewLifecycleOwner, Observer {
            viewModel.onTextChange(it)
        })

        viewModel.pinned.distinctUntilChanged().ignoreFirstValue().observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), it.takeIf { it }?.let {  "Pinned" } ?: "Unpinned", Toast.LENGTH_SHORT).show()
        })

            viewModel.colour.observe(viewLifecycleOwner, Observer {
            println("Ignoring background colour for now $it")
            //binding.noteColour = it
        })

        createNote_back.setOnClickListener { findNavController(this@NewNoteFragment).navigate(R.id.nav_home) }

        createNote_pin.setOnClickListener { viewModel.onPin() }

        viewModel.archived.observe(viewLifecycleOwner, Observer { archived ->
            if(archived) {
                createNote_archive.setImageResource(R.drawable.ic_upload)
                createNote_archive.setOnClickListener {
                    viewModel.onArchive()
                }
            } else {
                createNote_archive.setImageResource(R.drawable.ic_package)
                createNote_archive.setOnClickListener {
                    viewModel.onArchive()
                    findNavController().popBackStack()
                }
            }
        })

        createNote_newItem.setOnClickListener { viewModel.addCheckbox() }

        BottomSheetBehavior.from(binding.createNoteMoreSheet).state = BottomSheetBehavior.STATE_HIDDEN
        BottomSheetBehavior.from(binding.createNoteAddSheet).state = BottomSheetBehavior.STATE_HIDDEN


            viewModel.deletionTime.observe(viewLifecycleOwner, Observer {
            if(it == null) {
                createNote_more_share.visibility = View.VISIBLE
                createNote_more_labels.visibility = View.VISIBLE
                createNote_more_colors.visibility = View.VISIBLE
                createNote_more_copy.visibility = View.VISIBLE
                createNote_more_delete.visibility = View.VISIBLE
                createNote_more_restore.visibility = View.GONE
                createNote_more_deletePerm.visibility = View.GONE
            } else {
                createNote_more_share.visibility = View.GONE
                createNote_more_labels.visibility = View.GONE
                createNote_more_colors.visibility = View.GONE
                createNote_more_copy.visibility = View.GONE
                createNote_more_delete.visibility = View.GONE
                createNote_more_restore.visibility = View.VISIBLE
                createNote_more_deletePerm.visibility = View.VISIBLE
            }
        })

        binding.createNoteMoreRestore.setOnClickListener {
            viewModel.onCheckboxRestore()
        }

        binding.createNoteMoreDeletePerm.setOnClickListener {
            viewModel.onCheckboxDeletePermanently()
            findNavController().popBackStack()
        }

        createNote_more.setOnClickListener {
            BottomSheetBehavior.from(createNote_moreSheet).apply {
                state = if(state == BottomSheetBehavior.STATE_HIDDEN) {
                    BottomSheetBehavior.STATE_EXPANDED
                } else {
                    BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }

        binding.createNoteAdd.setOnClickListener {
            BottomSheetBehavior.from(binding.createNoteAddSheet).apply {
                state = if(state == BottomSheetBehavior.STATE_HIDDEN) {
                    BottomSheetBehavior.STATE_EXPANDED
                } else {
                    BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }
        binding.createNoteAddPhoto.setOnClickListener { AddVideoDialog(requireActivity(), viewModel.storageManager).takeVideo() }
        binding.createNoteAddImage.setOnClickListener { AddVideoDialog(requireActivity(), viewModel.storageManager).pickVideo() }
        binding.createNoteAddCheckbox.setOnClickListener { viewModel.addCheckbox() }


        createNote_reminder.setOnClickListener {
            ReminderDialog(requireActivity()) { zonedDateTime ->
                viewModel.setNoteReminder(zonedDateTime)
            }.simpleDialog()

        }

        createNote_more_delete.setOnClickListener {
            viewModel.onDelete()
            findNavController().popBackStack()
        }

        createNote_more_labels.setOnClickListener {
            findNavController(this@NewNoteFragment).navigate(R.id.action_nav_new_note_to_nav_note_label_edit, Bundle().apply {
                putLong(NoteLabelEditFragment.KEY_NOTE_ID, viewModel.noteAndCheckboxes!!.value!!.note.id)
            })
        }

        binding.createNoteMoreCopy.visibility = View.GONE
        binding.createNoteMoreShare.visibility = View.GONE
    }
}
