package pw.cub3d.cub3_notes.core.manager

import android.Manifest
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.coroutinespermission.PermissionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioManager @Inject constructor(
    private val storageManager: StorageManager
) {

    private var recordingSession = MediaRecorder()
    private var outputFile: File? = null

    suspend fun startRecording(frag: Fragment) = withContext(Dispatchers.IO) {

        val permissionResult = PermissionManager.requestPermissions(
            frag,
            2,
            Manifest.permission.RECORD_AUDIO
        )

        when (permissionResult) {
            is PermissionResult.PermissionGranted -> {
                outputFile = File.createTempFile("audio", "aac")

                println("Started recording audio to ${outputFile!!.path}")

                recordingSession.apply {
                    setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION)
                    setOutputFormat(MediaRecorder.OutputFormat.DEFAULT)
                    setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
                    setOutputFile(outputFile!!.path)
                    prepare()
                    start()
                }
            }
            is PermissionResult.PermissionDenied -> {
                Toast.makeText(frag.requireContext(), "Mic permission needed", Toast.LENGTH_SHORT).show()
            }
            is PermissionResult.ShowRational -> {
                Toast.makeText(frag.requireContext(), "Mic permission needed", Toast.LENGTH_SHORT).show()
            }
            is PermissionResult.PermissionDeniedPermanently -> {
                Toast.makeText(frag.requireContext(), "Mic permission needed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun stopRecording(): File? {
        println("Stopping recording")
        outputFile?.let { outputFile ->
            recordingSession.stop()
            recordingSession.reset()
            val perminantFile = storageManager.getNewAudioFile()
            outputFile.copyTo(perminantFile)

            return perminantFile
        }
        return null
    }

    fun playAudio(name: String) {
        val mp = MediaPlayer()

        try {
            mp.setDataSource(File(storageManager.getAudioDir(), name).path)
            mp.prepare()
            mp.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}