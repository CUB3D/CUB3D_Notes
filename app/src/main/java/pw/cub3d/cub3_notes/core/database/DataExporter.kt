package pw.cub3d.cub3_notes.core.database

import android.content.Context
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import java.io.File
import java.nio.charset.Charset
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pw.cub3d.cub3_notes.core.database.dao.ImageDao
import pw.cub3d.cub3_notes.core.database.entity.NoteAndCheckboxes
import pw.cub3d.cub3_notes.core.database.repository.NoteRepository

class DataExporter @Inject constructor(
    private val notesRepository: NoteRepository,
    private val imageDao: ImageDao,
    private val context: Context
) {
    suspend fun exportToFile(file: File) {
        withContext(Dispatchers.IO) {
            file.bufferedWriter(Charset.forName("utf-8")).apply {
                val data = generateJsonString(
                    DataExportFormat(notesRepository.getAllNotes(), imageDao.getAll().map { ExportedImage(it.imageName, it.toBase64(context)) })
                )
                write(data)
                newLine()
                flush()
                close()
            }
        }
    }

    private fun generateJsonString(notes: DataExportFormat): String {
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(DataExportFormat::class.java)
        return adapter.toJson(notes)
    }
}

@JsonClass(generateAdapter = true)
data class DataExportFormat(
    val notes: List<NoteAndCheckboxes>,
    val images: List<ExportedImage>
)

@JsonClass(generateAdapter = true)
data class ExportedImage(
    val name: String,
    val base64: String
)
