package pw.cub3d.cub3_notes.database

import android.content.Context
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pw.cub3d.cub3_notes.database.dao.ImageDao
import pw.cub3d.cub3_notes.database.entity.NoteAndCheckboxes
import pw.cub3d.cub3_notes.database.repository.NoteRepository
import java.io.File
import java.nio.charset.Charset
import java.security.PrivateKey
import javax.inject.Inject

class DataExporter @Inject constructor(
    private val notesRepository: NoteRepository,
    private val imageDao: ImageDao,
    private val context: Context
) {
    suspend fun exportToFile(file: File) {
        withContext(Dispatchers.IO) {
            file.bufferedWriter(Charset.forName("utf-8")).apply {
                val data = generateJsonString(
                    DataExportFormat(notesRepository.getAllNotes(), imageDao.getAll().map { it.toBase64(context) })
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
    val images: List<String>
)