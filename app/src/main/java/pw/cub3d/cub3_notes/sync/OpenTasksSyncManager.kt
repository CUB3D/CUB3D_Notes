package pw.cub3d.cub3_notes.sync

import android.content.ContentProviderClient
import android.content.Context
import android.net.Uri
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class OpenTasksSyncManager @Inject constructor(
    private val context: Context
) {
    private var contentProviderClient: ContentProviderClient?

    init {
        val yourURI = Uri.parse("content://org.dmfs.provider/YourDatabase")
        contentProviderClient = context.contentResolver.acquireContentProviderClient(yourURI)
    }
}
