package pw.cub3d.cub3_notes.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import pw.cub3d.cub3_notes.core.database.dao.*
import pw.cub3d.cub3_notes.core.database.entity.*

object Migrations {
    val MIGRATE_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE notes ADD COLUMN pinned INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE notes ADD COLUMN archived INTEGER NOT NULL DEFAULT 0")
            database.execSQL("ALTER TABLE notes ADD COLUMN modificationTime TEXT NOT NULL default ''")
        }
    }

    val MIGRATE_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE notes ADD COLUMN type TEXT NOT NULL DEFAULT ''")
        }
    }

    val MIGRATE_3_4 = object : Migration(3, 4) {
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

    val MIGRATE_4_5 = object : Migration(4, 5) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE notes ADD COLUMN timeReminder TEXT")
        }
    }

    val MIGRATE_5_6 = object : Migration(5, 6) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""CREATE TABLE labels(
                                    id INTEGER PRIMARY KEY NOT NULL DEFAULT 0, 
                                    title TEXT NOT NULL DEFAULT '',
                                    creation_date TEXT NOT NULL DEFAULT ''
                            )""".trimMargin())
        }
    }

    val MIGRATE_6_7 = object : Migration(6, 7) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""CREATE TABLE note_label(
                                    id INTEGER PRIMARY KEY NOT NULL DEFAULT 0,
                                    note_id INTEGER NOT NULL DEFAULT 0,
                                    label_id INTEGER NOT NULL DEFAULT 0
                            )""".trimMargin())
        }
    }

    val MIGRATE_7_8 = object : Migration(7, 8) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""CREATE TABLE card_colour(
                                    id INTEGER PRIMARY KEY NOT NULL DEFAULT 0,
                                    hex_colour TEXT NOT NULL DEFAULT ''
                            )""".trimMargin())
        }
    }

    val MIGRATE_8_9 = object : Migration(8, 9) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("INSERT INTO card_colour (hex_colour) VALUES ('#ff0000')")
        }
    }

    val MIGRATE_9_10 = object : Migration(9, 10) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE notes ADD COLUMN colour TEXT NOT NULL DEFAULT '#ffffff'")
        }
    }

    val MIGRATE_10_11 = object : Migration(10, 11) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""CREATE TABLE image(
                                    id INTEGER PRIMARY KEY NOT NULL DEFAULT 0,
                                    noteId INTEGER NOT NULL DEFAULT 0,
                                    imageName TEXT NOT NULL DEFAULT ``,
                                    FOREIGN KEY (noteId) REFERENCES notes(id)
                            )""".trimMargin())
        }
    }

    val MIGRATE_11_12 = object : Migration(11, 12) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE INDEX index_note_label ON note_label (note_id, label_id)")
        }
    }

    val MIGRATE_12_13 = object : Migration(12, 13) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE notes ADD COLUMN deletionTime TEXT")
        }
    }

    val MIGRATE_13_14 = object : Migration(13, 14) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE checkbox_entry ADD COLUMN position INTEGER DEFAULT 1 NOT NULL")
        }
    }

    val MIGRATE_14_15 = object : Migration(14, 15) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE notes ADD COLUMN position INTEGER DEFAULT 0 NOT NULL")
            database.execSQL("UPDATE notes SET position = id")
        }
    }

    val MIGRATE_15_16 = object : Migration(15, 16) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""CREATE TABLE audio(
                                    id INTEGER PRIMARY KEY NOT NULL DEFAULT 0,
                                    noteId INTEGER NOT NULL DEFAULT 0,
                                    fileName TEXT NOT NULL DEFAULT ``,
                                    FOREIGN KEY (noteId) REFERENCES notes(id)
                            )""".trimMargin())
        }
    }

    val MIGRATE_16_17 = object : Migration(16, 17) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""CREATE TABLE video(
                                    id INTEGER PRIMARY KEY NOT NULL DEFAULT 0,
                                    noteId INTEGER NOT NULL DEFAULT 0,
                                    fileName TEXT NOT NULL DEFAULT ``,
                                    FOREIGN KEY (noteId) REFERENCES notes(id)
                            )""".trimMargin())
        }
    }

    val MIGRATE_17_18 = object : Migration(17, 18) {
        override fun migrate(database: SupportSQLiteDatabase) {

            database.execSQL("PRAGMA foreign_keys=OFF")

            database.execSQL("""CREATE TABLE new_notes(
                                    id INTEGER PRIMARY KEY NOT NULL DEFAULT 0,
                                    title TEXT NOT NULL DEFAULT ``,
                                    text TEXT NOT NULL DEFAULT ``,
                                    pinned INTEGER NOT NULL DEFAULT 0,
                                    archived INTEGER NOT NULL DEFAULT 0,
                                    modificationTime TEXT NOT NULL DEFAULT ``,
                                    type TEXT NOT NULL DEFAULT ``,
                                    timeReminder TEXT DEFAULT null,
                                    colour TEXT DEFAULT null,
                                    deletionTime TEXT DEFAULT null,
                                    position INTEGER NOT NULL DEFAULT 0,
                                    creationTime TEXT NOT NULL DEFAULT ``,
                                    viewTime TEXT NOT NULL DEFAULT ``
                            )""".trimMargin())

            database.execSQL("INSERT INTO new_notes SELECT id, title, text, pinned, archived, modificationTime, type, timeReminder, null, deletionTime, position, modificationTime, modificationTime FROM notes")

            database.execSQL("DROP TABLE notes")

            database.execSQL("ALTER TABLE new_notes RENAME TO notes")

            database.execSQL("PRAGMA foreign_keys=ON")
        }
    }

    val MIGRATE_18_19 = object : Migration(18, 19) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE labels ADD COLUMN colour TEXT DEFAULT null")
        }
    }

    val MIGRATE_19_20 = object : Migration(19, 20) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE notes ADD COLUMN hiddenContent INTEGER NOT NULL DEFAULT 0")
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
        ImageEntry::class,
        AudioEntry::class,
        VideoEntry::class
    ],
    version = 20,
    exportSchema = true
)
abstract class RoomDB : RoomDatabase() {
    abstract fun notesDao(): NotesDao
    abstract fun checkboxEntryDao(): CheckboxEntryDao
    abstract fun labelDao(): LabelDao
    abstract fun colourDao(): ColourDao
    abstract fun imageDao(): ImageDao
    abstract fun audioDao(): AudioDao
    abstract fun videoDao(): VideoDao

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
                    Migrations.MIGRATE_11_12,
                    Migrations.MIGRATE_12_13,
                    Migrations.MIGRATE_13_14,
                    Migrations.MIGRATE_14_15,
                    Migrations.MIGRATE_15_16,
                    Migrations.MIGRATE_16_17,
                    Migrations.MIGRATE_17_18,
                    Migrations.MIGRATE_18_19,
                    Migrations.MIGRATE_19_20
                // TODO: used for searching for labels, bad, should remove somehow
                ).allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
