package pw.cub3d.cub3_notes

import android.content.Context
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageManager @Inject constructor(
    private val context: Context
){
    private var lastCameraImageFile: File? = null

    fun getImagesDir() = File(context.filesDir, "images/").apply { mkdirs() }
    fun getAudioDir() = File(context.filesDir, "audio/").apply { mkdirs() }

    fun getNewImageFile() = File(getImagesDir(), UUID.randomUUID().toString())
    fun getNewAudioFile() = File(getAudioDir(), UUID.randomUUID().toString())

    fun getCameraImageFile() = getNewImageFile().apply {
        lastCameraImageFile = this
    }

    fun getLastCameraImageFile() = lastCameraImageFile
    fun getLastCameraImageUUID() = lastCameraImageFile?.name
}