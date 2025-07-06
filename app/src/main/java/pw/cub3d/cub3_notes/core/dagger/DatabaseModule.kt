package pw.cub3d.cub3_notes.core.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
import pw.cub3d.cub3_notes.core.database.RoomDB

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideNotesDao(r: RoomDB) = r.notesDao()
    @Singleton
    @Provides
    fun provideCheckboxEntityDao(r: RoomDB) = r.checkboxEntryDao()
    @Singleton
    @Provides
    fun provideLabelDao(r: RoomDB) = r.labelDao()
    @Singleton
    @Provides
    fun provideColoursDao(r: RoomDB) = r.colourDao()
    @Singleton
    @Provides
    fun provideImageDao(r: RoomDB) = r.imageDao()
    @Singleton
    @Provides
    fun provideAudioDao(r: RoomDB) = r.audioDao()
    @Singleton
    @Provides
    fun provideVideoDao(r: RoomDB) = r.videoDao()

    @Singleton
    @Provides
    fun provideRoomInstance(ctx: Context) = RoomDB.getDatabase(ctx)
}
