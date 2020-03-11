package pw.cub3d.cub3_notes.ui.dialog.addImage

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Window
import kotlinx.android.synthetic.main.dialog_add_image.*
import pw.cub3d.cub3_notes.R
import java.io.File


class AddImageDialog(
    private val act: Activity
): Dialog(act) {

    companion object {
        const val PICK_IMAGE = 1
        const val TAKE_PHOTO = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.dialog_add_image)

        addImage_chooseImage.setOnClickListener {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
            act.startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE
            )
        }

        addImage_takePhoto.setOnClickListener {
            val file = File(
                    context
                    .getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
                    .absolutePath + File.separator.toString() + "yourPicture.jpg"
            )

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file))
            }

            act.startActivityForResult(cameraIntent, TAKE_PHOTO)
        }
    }
}