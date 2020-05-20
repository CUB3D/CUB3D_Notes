package pw.cub3d.cub3_notes.ui.dialog.addVideo

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.Window
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.dialog_add_video.*
import pw.cub3d.cub3_notes.BuildConfig
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.core.manager.StorageManager

class AddVideoDialog(
    private val act: Activity,
    private val storageManager: StorageManager,
    private val noteId: Long? = null
) : Dialog(act) {

    companion object {
        const val PICK_VIDEO = 3
        const val TAKE_VIDEO = 4

        private var lastDialogInstance: AddVideoDialog? = null
        var lastNoteId: Long? = null

        fun closeIfOpen() {
            lastDialogInstance?.dismiss()
        }
    }

    init {
        lastDialogInstance = this
        lastNoteId = noteId
    }

    fun pickVideo() {
        val intent = Intent().apply {
            type = "video/*"
            action = Intent.ACTION_GET_CONTENT
        }
        act.startActivityForResult(
            Intent.createChooser(intent, "Select Video"),
            PICK_VIDEO
        )
    }

    fun takeVideo() {
        val file = storageManager.getCameraVideoFile()

        val uri = FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID.toString() + ".provider",
            file
        )

        val cameraIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }

        act.startActivityForResult(cameraIntent, TAKE_VIDEO)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.dialog_add_video)

        addVideo_chooseImage.setOnClickListener {
            pickVideo()
        }

        addVideo_takeVideo.setOnClickListener {
            takeVideo()
        }
    }
}
