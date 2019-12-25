package pw.cub3d.cub3_notes.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import pw.cub3d.cub3_notes.database.RoomDB
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideNotesDao(database: RoomDB) = database.notesDao()
    @Singleton
    @Provides
    fun provideCheckboxEntityDao(database: RoomDB) = database.checkboxEntryDao()



    @Singleton
    @Provides
    fun provideRoomInstance(ctx: Context) = RoomDB.getDatabase(ctx)
}