package pw.cub3d.cub3_notes.database

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pw.cub3d.cub3_notes.database.entity.NoteAndCheckboxes
import pw.cub3d.cub3_notes.database.repository.NoteRepository
import java.io.File
import java.nio.charset.Charset
import javax.inject.Inject

class DataExporter @Inject constructor(
    private val notesRepository: NoteRepository
) {
    suspend fun exportToFile(file: File) {
        withContext(Dispatchers.IO) {
            file.bufferedWriter(Charset.forName("utf-8")).apply {
                val data = generateJsonString(notesRepository.getAllNotes())
                write(data)
                newLine()
                flush()
                close()
            }
        }
    }

    private fun generateJsonString(notes: List<NoteAndCheckboxes>): String {
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter<List<NoteAndCheckboxes>>(
            Types.newParameterizedType(List::class.java, NoteAndCheckboxes::class.java)
        )
        return adapter.toJson(notes)
    }
}