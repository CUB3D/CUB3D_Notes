package pw.cub3d.cub3_notes.ui.dialog.addImage

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.Window
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import pw.cub3d.cub3_notes.BuildConfig
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.core.manager.StorageManager
import pw.cub3d.cub3_notes.databinding.DialogAddImageBinding

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

        val v: DialogAddImageBinding = DataBindingUtil.inflate(layoutInflater, R.layout.dialog_add_image, null, false)
        setContentView(v.root)
        
        v.addImageChooseImage.setOnClickListener {
            pickImage()
        }

        v.addImageTakePhoto.setOnClickListener {
            takePhoto()
        }
    }
}
