package pw.cub3d.cub3_notes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import pw.cub3d.cub3_notes.database.dao.*
import pw.cub3d.cub3_notes.database.entity.*

object Migrations {
    val MIGRATE_1_2 = object: Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE notes ADD COLUMN pinned INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE notes ADD COLUMN archived INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE notes ADD COLUMN modificationTime TEXT NOT NULL default ''")
        }
    }

    val MIGRATE_2_3 = object: Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE notes ADD COLUMN type TEXT NOT NULL DEFAULT ''")
        }
    }

    val MIGRATE_3_4 = object: Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""CREATE TABLE checkbox_entry(
                id INTEGER PRIMARY KEY NOT NULL DEFAULT 0,
                noteId INTEGER NOT NULL DEFAULT 0,
                content TEXT NOT NULL DEFAULT '',
                checked INTEGER NOT NULL DEFAULT 0,
                indentLevel INTEGER NOT NULL DEFAULT 0
            )""".trimMargin())
        }
    }

    val MIGRATE_4_5 = object: Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE notes ADD COLUMN timeReminder TEXT")
        }
    }

    val MIGRATE_5_6 = object: Migration(5, 6) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""CREATE TABLE labels(
                                    id INTEGER PRIMARY KEY NOT NULL DEFAULT 0, 
                                    title TEXT NOT NULL DEFAULT '',
                                    creation_date TEXT NOT NULL DEFAULT ''
                            )""".trimMargin())
        }
    }

    val MIGRATE_6_7 = object: Migration(6, 7) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""CREATE TABLE note_label(
                                    id INTEGER PRIMARY KEY NOT NULL DEFAULT 0,
                                    note_id INTEGER NOT NULL DEFAULT 0,
                                    label_id INTEGER NOT NULL DEFAULT 0
                            )""".trimMargin())
        }
    }

    val MIGRATE_7_8 = object: Migration(7, 8) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""CREATE TABLE card_colour(
                                    id INTEGER PRIMARY KEY NOT NULL DEFAULT 0,
                                    hex_colour TEXT NOT NULL DEFAULT ''
                            )""".trimMargin())
        }
    }

    val MIGRATE_8_9 = object: Migration(8, 9) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("INSERT INTO card_colour (hex_colour) VALUES ('#ff0000')")
        }
    }

    val MIGRATE_9_10 = object: Migration(9, 10) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE notes ADD COLUMN colour TEXT NOT NULL DEFAULT '#ffffff'")
        }
    }

    val MIGRATE_10_11 = object: Migration(10, 11) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""CREATE TABLE image(
                                    id INTEGER PRIMARY KEY NOT NULL DEFAULT 0,
                                    noteId INTEGER NOT NULL DEFAULT 0,
                                    imageName TEXT NOT NULL DEFAULT ``,
                                    FOREIGN KEY (noteId) REFERENCES notes(id)
                            )""".trimMargin())
        }
    }

    val MIGRATE_11_12 = object: Migration(11, 12) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE INDEX index_note_label ON note_label (note_id, label_id)")
        }
    }
}

@Database(
    entities = [
        Note::class,
        CheckboxEntry::class,
        Label::class,
        NoteLabel::class,
        Colour::class,
        ImageEntry::class
    ],
    version = 12,
    exportSchema = true
)
abstract class RoomDB: RoomDatabase() {
    abstract fun notesDao(): NotesDao
    abstract fun checkboxEntryDao(): CheckboxEntryDao
    abstract fun labelDao(): LabelDao
    abstract fun colourDao(): ColourDao
    abstract fun imageDao(): ImageDao

    companion object {
        @Volatile
        private var INSTANCE: RoomDB? = null

        fun getDatabase(ctx: Context): RoomDB {
            val temp = INSTANCE
            if (temp != null) {
                return temp
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext,
                    RoomDB::class.java,
                    "ncl_database"
                ).addMigrations(
                    Migrations.MIGRATE_1_2,
                    Migrations.MIGRATE_2_3,
                    Migrations.MIGRATE_3_4,
                    Migrations.MIGRATE_4_5,
                    Migrations.MIGRATE_5_6,
                    Migrations.MIGRATE_6_7,
                    Migrations.MIGRATE_7_8,
                    Migrations.MIGRATE_8_9,
                    Migrations.MIGRATE_9_10,
                    Migrations.MIGRATE_10_11,
                    Migrations.MIGRATE_11_12
                //TODO: used for searching for labels, bad, should remove somehow
                ).allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}