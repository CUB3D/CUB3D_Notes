package pw.cub3d.cub3_notes.sync

import android.content.ContentProviderClient
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import pw.cub3d.cub3_notes.database.dao.NotesDao
import javax.inject.Inject
import javax.inject.Singleton


//https://github.com/dmfs/opentasks/blob/88a57514b90bc3bdfd81b28e7af24ed52078545e/opentasks-contract/src/main/java/org/dmfs/tasks/contract/TaskContract.java
//https://github.com/dmfs/opentasks/tree/master/opentasks-provider/src/main/java/org/dmfs/provider/tasks
@Singleton
class OpenTasksSyncManager @Inject constructor(
    private val context: Context,
    private val notesDao: NotesDao
) {
    private var contentProviderClient: ContentProviderClient?

    init {
        val yourURI = Uri.parse("content://org.dmfs.tasks/")
        contentProviderClient = context.contentResolver.acquireContentProviderClient(yourURI)
    }

    fun test() {
        val t = contentProviderClient!!.query(Uri.parse("content://org.dmfs.tasks/tasklists"), null, null, null, null)!!

        println("TEST")
        while(t.moveToNext()) {
            println(t.columnNames.joinToString(separator = ", ") { it })
            print(t.getString(0) + ", ")
            print(t.getString(1) + ", ")
            println(t.getString(2))
            println(t.getString(3))
        }
        t.close()

        //https://github.com/dmfs/opentasks/blob/88a57514b90bc3bdfd81b28e7af24ed52078545e/opentasks-contract/src/main/java/org/dmfs/tasks/contract/TaskContract.java#L637
        val tt = contentProviderClient!!.insert(Uri.parse("content://org.dmfs.tasks/tasks"), ContentValues().apply {
            put("list_id", 11) //TODO: retrieve from abovfe
            put("title", "HelloWorld")
            put("description", "description")
        })
        println(tt)

//        val collection = CalDAVCollection("http://radicale.home/", HostConfiguration.ANY_HOST_CONFIGURATION, CalDAV4JMethodFactory(), "OpenNotes")
//        try {
//            collection.testConnection(HttpClient().apply {
//                state.setCredentials(AuthScope.ANY, UsernamePasswordCredentials("cub3d", "Callum21"))
//            })
//        } catch (e: CalDAV4JException) {
//            e.cause?.printStackTrace()
//            e.printStackTrace()
//        }
//
//        val cal = collection.getCalendar(HttpClient().apply {
//            state.setCredentials(AuthScope.ANY, UsernamePasswordCredentials("cub3d", "Callum21"))
//        }, "e2675323-362c-e3bf-ea48-9d972bd365d1")
//
//        // Clear the calendar
//        cal.components.forEach {
//            cal.components.remove(it)
//        }
//
//        GlobalScope.launch {
//            notesDao.getAllNotes().forEach {
//                cal.components.add(VToDo(Date(System.currentTimeMillis()), it.note.title))
//            }
//        }
    }
}
