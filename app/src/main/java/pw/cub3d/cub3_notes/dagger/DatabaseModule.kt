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
    fun RoomDB.provideNotesDao() = notesDao()
    @Singleton
    @Provides
    fun RoomDB.provideCheckboxEntityDao() = checkboxEntryDao()
    @Singleton
    @Provides
    fun RoomDB.provideLabelDao() = labelDao()
    @Singleton
    @Provides
    fun RoomDB.provideColoursDao() = colourDao()
    @Singleton
    @Provides
    fun RoomDB.provideImageDao() = imageDao()
    @Singleton
    @Provides
    fun RoomDB.provideAudioDao() = audioDao()
    @Singleton
    @Provides
    fun RoomDB.provideVideoDao() = videoDao()

    @Singleton
    @Provides
    fun provideRoomInstance(ctx: Context) = RoomDB.getDatabase(ctx)
}