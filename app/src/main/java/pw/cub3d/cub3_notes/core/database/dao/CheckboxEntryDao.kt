package pw.cub3d.cub3_notes.core.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import pw.cub3d.cub3_notes.core.database.entity.CheckboxEntry

@Dao
abstract class CheckboxEntryDao {
    @Insert
    abstract fun insert(data: CheckboxEntry): Long
    @Update
    abstract fun update(data: CheckboxEntry)

    @Query("SELECT * FROM checkbox_entry WHERE noteId = :noteId")
    abstract fun getByNote(noteId: Long): List<CheckboxEntry>
    @Query("SELECT * FROM checkbox_entry WHERE noteId = :noteId")
    abstract fun getByNoteLive(noteId: Long): LiveData<List<CheckboxEntry>>

    fun save(entry: CheckboxEntry) {
        // If the note dose't exist yet
        if(entry.id == 0L) {
            println("Inserted entry: $entry")
            entry.id = insert(entry)
        } else {
            update(entry)
            println("Updated entry: $entry")
        }
    }

    @Query("DELETE FROM checkbox_entry WHERE id = :id")
    abstract fun delete(id: Long)

    @Transaction
    open fun saveAll(checkboxEntry: List<CheckboxEntry>) {
        checkboxEntry.forEach { save(it) }
    }

    @Query("UPDATE checkbox_entry SET checked = :checked WHERE id = :id")
    abstract fun setChecked(id: Long, checked: Boolean)

    @Query("UPDATE checkbox_entry SET content = :text WHERE id = :id")
    abstract fun setText(id: Long, text: String)

    @Query("UPDATE checkbox_entry SET position = :position WHERE id = :id")
    abstract fun setPosition(id: Long, position: Int)

    @Transaction
    open fun setPositions(positions: List<Pair<Long, Int>>) {
        positions.forEach {
            setPosition(it.first, it.second)
        }
    }
}
