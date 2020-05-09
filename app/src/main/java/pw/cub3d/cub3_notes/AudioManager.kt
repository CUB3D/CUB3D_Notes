package pw.cub3d.cub3_notes

import android.media.MediaPlayer
import android.media.MediaRecorder
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioManager @Inject constructor(
    private val storageManager: StorageManager
) {

    private var recordingSession = MediaRecorder()
    private var outputFile: File? = null

    fun startRecording() {
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