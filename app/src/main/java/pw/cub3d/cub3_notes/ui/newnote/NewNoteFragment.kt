package pw.cub3d.cub3_notes.ui.newnote

import android.graphics.Canvas
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
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.File
import kotlin.math.absoluteValue
import kotlinx.coroutines.runBlocking
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.core.dagger.injector
import pw.cub3d.cub3_notes.core.database.entity.Note
import pw.cub3d.cub3_notes.core.utils.distinctUntilChangedBy
import pw.cub3d.cub3_notes.core.utils.ignoreFirstValue
import pw.cub3d.cub3_notes.databinding.FragmentNewNoteBinding
import pw.cub3d.cub3_notes.ui.dialog.addImage.AddImageDialog
import pw.cub3d.cub3_notes.ui.dialog.addVideo.AddVideoDialog
import pw.cub3d.cub3_notes.ui.dialog.reminderdialog.ReminderDialog
import pw.cub3d.cub3_notes.ui.nav.NewNoteNavigationController
import pw.cub3d.cub3_notes.ui.newnote.imagelist.ImageEditAdapter
import pw.cub3d.cub3_notes.ui.noteLabels.NoteLabelEditFragment

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

        viewModel.noteAndCheckboxes!!.observe(viewLifecycleOwner, Observer { note ->
            if (note.checkboxes.isNotEmpty() || note.note.type == Note.TYPE_CHECKBOX) {
                binding.createNoteText.visibility = View.GONE
                binding.createNoteCheckBoxes.visibility = View.VISIBLE
                binding.createNoteNewItem.visibility = View.VISIBLE
            } else if (note.note.type == Note.TYPE_TEXT) {
                binding.createNoteText.visibility = View.VISIBLE
                binding.createNoteCheckBoxes.visibility = View.GONE
                binding.createNoteNewItem.visibility = View.GONE
            }
        })

        viewModel.modificationTime.observe(viewLifecycleOwner, Observer {
            binding.createNoteLastEdited.text = it
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
                // TODO: release the player

                binding.createNoteVideo.visibility = View.VISIBLE
            }
        })

        binding.createNoteAudio.layoutManager = LinearLayoutManager(requireContext())
        audioAdapter = AudioAdapter(requireContext())
        binding.createNoteAudio.adapter = audioAdapter
        viewModel.audioClips.observe(viewLifecycleOwner, Observer { audioAdapter.submitList(it) })

        binding.createNoteCheckBoxes.layoutManager = LinearLayoutManager(requireContext())
        checkBoxAdapter = CheckBoxAdapter(requireContext(), viewModel, this.viewLifecycleOwner)
        binding.createNoteCheckBoxes.adapter = checkBoxAdapter

        val callback: ItemTouchHelper.Callback = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                println("Get chkbox move flags")

                val selectedItem =
                    checkBoxAdapter.items.find { it.id == viewHolder.itemId }!!

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
                val oldList = ArrayList(checkBoxAdapter.items)
                val selectedItem = oldList.find { it.id == viewHolder.itemId }!!
                val targetIndex = oldList.indexOf(oldList.find { it.id == target.itemId })
                oldList.remove(selectedItem)
                oldList.add(targetIndex, selectedItem)

                val newPositions = oldList.mapIndexed { index, checkboxEntry ->
                    checkboxEntry.id to index
                }

                viewModel.upadateCheckboxPosition(newPositions)

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                println("on chkbox swip")
                if (direction == ItemTouchHelper.LEFT) {
                    viewModel.onCheckboxChecked(
                        checkBoxAdapter.items[viewHolder.adapterPosition],
                        true
                    )
                }

                if (direction == ItemTouchHelper.RIGHT) {
                    viewModel.onCheckboxChecked(
                        checkBoxAdapter.items[viewHolder.adapterPosition],
                        false
                    )
                }
                checkBoxAdapter.notifyItemChanged(viewHolder.adapterPosition)
            }

            override fun onChildDrawOver(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder?,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDrawOver(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

                val baseView = (viewHolder as CheckBoxViewHolder).view

                val dXRelative: Float = ((dX / baseView.checkboxEntryBaselayout.width) * 3).coerceAtMost(1f).coerceAtLeast(-1f)
                println("dx: $dXRelative")

                if (dXRelative < 0) {
                    val absDx = dXRelative.absoluteValue
                    baseView.checkboxEntryCheckAnimation
                        .animate()
                        .scaleX(absDx)
                        .scaleY(absDx)
                        .setDuration(0)
                        .start()
                    getDefaultUIUtil().onDrawOver(
                        c,
                        recyclerView,
                        baseView.checkboxEntryCheckAnimation,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )

                    baseView.checkboxEntryCheckAnimation.visibility = View.VISIBLE
                    baseView.checkboxEntryDeleteAnimation.visibility = View.GONE
                }

                if (dXRelative > 0) {
                    baseView.checkboxEntryDeleteAnimation
                        .animate()
                        .scaleX(dXRelative)
                        .scaleY(dXRelative)
                        .setDuration(0)
                        .start()
                    getDefaultUIUtil().onDrawOver(
                        c,
                        recyclerView,
                        baseView.checkboxEntryCheckAnimation,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )

                    baseView.checkboxEntryCheckAnimation.visibility = View.GONE
                    baseView.checkboxEntryDeleteAnimation.visibility = View.VISIBLE
                }
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(binding.createNoteCheckBoxes)

        // TODO sort in room junction
        viewModel.checkboxes.map { it.sortedBy { it.position } }.distinctUntilChangedBy { old, new -> old.map { it.id } == new.map { it.id } }.observe(viewLifecycleOwner, Observer { checkboxes ->
            println("Updating checkboxes: $checkboxes")
            binding.createNoteCheckBoxes.visibility = View.VISIBLE
            binding.createNoteAddCheckbox.visibility = View.VISIBLE
            checkBoxAdapter.submitList(checkboxes)
        })

//        checkBoxAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
//            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
//                val vh = binding.createNoteCheckBoxes.findViewHolderForAdapterPosition((binding.createNoteCheckBoxes.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()) as CheckBoxViewHolder
//                vh.view.checkboxEntryText.requestFocus()
//            }
//        })

        binding.createNoteMoreColors.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val colorAdapter = ColoursAdapter(this, viewModel)
        binding.createNoteMoreColors.adapter = colorAdapter
        viewModel.defaultNoteColours.observe(viewLifecycleOwner, Observer { colorAdapter.submitList(it) })


        binding.createNoteImage.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ImageEditAdapter(requireContext())
        binding.createNoteImage.adapter = adapter
        adapter.bindToLiveData(viewLifecycleOwner, viewModel.images)

        viewModel.title.distinctUntilChanged().ignoreFirstValue().observe(viewLifecycleOwner, Observer {
            viewModel.onTitleChange(it)
        })

        viewModel.text.distinctUntilChanged().ignoreFirstValue().observe(viewLifecycleOwner, Observer {
            viewModel.onTextChange(it)
        })

        viewModel.pinned.distinctUntilChanged().ignoreFirstValue().observe(viewLifecycleOwner, Observer {
            Toast.makeText(requireContext(), it.takeIf { it }?.let { "Pinned" } ?: "Unpinned", Toast.LENGTH_SHORT).show()
        })

            viewModel.colour.observe(viewLifecycleOwner, Observer {
            println("Ignoring background colour for now $it")
            // binding.noteColour = it
        })

        binding.createNoteBack.setOnClickListener { findNavController(this@NewNoteFragment).navigate(R.id.nav_home) }

        binding.createNotePin.setOnClickListener { viewModel.onPin() }

        viewModel.archived.observe(viewLifecycleOwner, Observer { archived ->
            if (archived) {
                binding.createNoteArchive.setImageResource(R.drawable.ic_upload)
                binding.createNoteArchive.setOnClickListener {
                    viewModel.onArchive()
                }
            } else {
                binding.createNoteArchive.setImageResource(R.drawable.ic_package)
                binding.createNoteArchive.setOnClickListener {
                    viewModel.onArchive()
                    findNavController().popBackStack()
                }
            }
        })

        binding.createNoteNewItem.setOnClickListener { viewModel.addCheckbox() }

        BottomSheetBehavior.from(binding.createNoteMoreSheet).state = BottomSheetBehavior.STATE_HIDDEN
        BottomSheetBehavior.from(binding.createNoteAddSheet).state = BottomSheetBehavior.STATE_HIDDEN

            viewModel.deletionTime.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                binding.createNoteMoreShare.visibility = View.VISIBLE
                binding.createNoteMoreLabels.visibility = View.VISIBLE
                binding.createNoteMoreColors.visibility = View.VISIBLE
                binding.createNoteMoreCopy.visibility = View.VISIBLE
                binding.createNoteMoreDelete.visibility = View.VISIBLE
                binding.createNoteMoreRestore.visibility = View.GONE
                binding.createNoteMoreDeletePerm.visibility = View.GONE
            } else {
                binding.createNoteMoreShare.visibility = View.GONE
                binding.createNoteMoreLabels.visibility = View.GONE
                binding.createNoteMoreColors.visibility = View.GONE
                binding.createNoteMoreCopy.visibility = View.GONE
                binding.createNoteMoreDelete.visibility = View.GONE
                binding.createNoteMoreRestore.visibility = View.VISIBLE
                binding.createNoteMoreDeletePerm.visibility = View.VISIBLE
            }
        })

        binding.createNoteMoreRestore.setOnClickListener {
            viewModel.onCheckboxRestore()
        }

        binding.createNoteMoreDeletePerm.setOnClickListener {
            viewModel.onCheckboxDeletePermanently()
            findNavController().popBackStack()
        }

        binding.createNoteMore.setOnClickListener {
            BottomSheetBehavior.from(binding.createNoteMoreSheet).apply {
                state = if (state == BottomSheetBehavior.STATE_HIDDEN) {
                    BottomSheetBehavior.STATE_EXPANDED
                } else {
                    BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }

        binding.createNoteAdd.setOnClickListener {
            BottomSheetBehavior.from(binding.createNoteAddSheet).apply {
                state = if (state == BottomSheetBehavior.STATE_HIDDEN) {
                    BottomSheetBehavior.STATE_EXPANDED
                } else {
                    BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }
        binding.createNoteAddImage.setOnClickListener { AddImageDialog(requireActivity(), viewModel.storageManager, viewModel.noteId!!).show() }
        binding.createNoteAddVideo.setOnClickListener { AddVideoDialog(requireActivity(), viewModel.storageManager, viewModel.noteId!!).show() }
        binding.createNoteAddCheckbox.setOnClickListener { viewModel.addCheckbox() }

        binding.createNoteReminder.setOnClickListener {
            ReminderDialog(requireActivity()) { zonedDateTime ->
                viewModel.setNoteReminder(zonedDateTime)
            }.simpleDialog()
        }

        binding.createNoteMoreDelete.setOnClickListener {
            viewModel.onDelete()
            findNavController().popBackStack()
        }

        binding.createNoteMoreLabels.setOnClickListener {
            findNavController(this@NewNoteFragment).navigate(R.id.action_nav_new_note_to_nav_note_label_edit, Bundle().apply {
                putLong(NoteLabelEditFragment.KEY_NOTE_ID, viewModel.noteAndCheckboxes!!.value!!.note.id)
            })
        }

        binding.createNoteMoreCopy.visibility = View.GONE
        binding.createNoteMoreShare.visibility = View.GONE
    }
}
