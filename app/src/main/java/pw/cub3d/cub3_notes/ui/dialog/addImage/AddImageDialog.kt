package pw.cub3d.cub3_notes.ui.dialog.addImage

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.Window
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.dialog_add_image.*
import pw.cub3d.cub3_notes.BuildConfig
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.core.manager.StorageManager

class AddImageDialog(
    private val act: Activity,
    private val storageManager: StorageManager,
    private val noteId: Long? = null
) : Dialog(act) {

    companion object {
        const val PICK_IMAGE = 1
        const val TAKE_PHOTO = 2

        private var lastDialogInstance: AddImageDialog? = null
        var lastNoteId: Long? = null

        fun closeIfOpen() {
            lastDialogInstance?.dismiss()
        }
    }

    init {
        lastDialogInstance = this
        lastNoteId = noteId
    }

    fun pickImage() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        act.startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            PICK_IMAGE
        )
    }

    fun takePhoto() {
        val file = storageManager.getCameraImageFile()

        val uri = FileProvider.getUriForFile(
            context,
            BuildConfig.APPLICATION_ID.toString() + ".provider",
            file
        )

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }

        act.startActivityForResult(cameraIntent, TAKE_PHOTO)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.dialog_add_image)

        addImage_chooseImage.setOnClickListener {
            pickImage()
        }

        addImage_takePhoto.setOnClickListener {
            takePhoto()
        }
    }
}
