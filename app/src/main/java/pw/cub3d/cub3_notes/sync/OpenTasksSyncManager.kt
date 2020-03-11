package pw.cub3d.cub3_notes.sync

import android.content.ContentProviderClient
import android.content.Context
import android.net.Uri
import javax.inject.Inject
import javax.inject.Singleton

//https://github.com/dmfs/opentasks/blob/88a57514b90bc3bdfd81b28e7af24ed52078545e/opentasks-contract/src/main/java/org/dmfs/tasks/contract/TaskContract.java
//https://github.com/dmfs/opentasks/tree/master/opentasks-provider/src/main/java/org/dmfs/provider/tasks
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
