package pw.cub3d.cub3_notes.activity

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import pw.cub3d.cub3_notes.Layouts
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.SettingsManager
import pw.cub3d.cub3_notes.StorageManager
import pw.cub3d.cub3_notes.ui.dialog.addImage.AddImageDialog
import pw.cub3d.cub3_notes.ui.nav.NewNoteNavigationController
import java.io.File
import java.util.*
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    lateinit var appBarConfiguration: AppBarConfiguration

    @Inject lateinit var newNoteNavigationController: NewNoteNavigationController

    @Inject lateinit var storageManager: StorageManager
    @Inject lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidInjection.inject(this)

        // Create notification channels
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(NotificationChannel("default", "Defualt", NotificationManager.IMPORTANCE_LOW))
            }
        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        obtainStyledAttributes(intArrayOf(R.attr.status_color)).apply {
            window.statusBarColor = this.getColor(0, Color.WHITE)
        }.recycle()

        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_new_note,
                R.id.nav_new_label
            ), drawer_layout
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->

            if(destination.id == R.id.nav_home) {
//                home_appBar.visibility = View.VISIBLE
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
//                home_appBar.visibility = View.GONE
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

        // Check if loaded due to notification click
        intent.getLongExtra("NOTE_ID", -1).takeIf { it > 0 }?.let {
            newNoteNavigationController.editNote(navController, it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.main_changeLayout) {
            if (settingsManager.noteLayout.value!! == Layouts.GRID) {
                settingsManager.noteLayout.postValue(Layouts.LIST)
                item.icon = resources.getDrawable(R.drawable.ic_rows, theme)
            }
            if (settingsManager.noteLayout.value!! == Layouts.LIST) {
                settingsManager.noteLayout.postValue(Layouts.GRID)
                item.icon = resources.getDrawable(R.drawable.ic_grid, theme)
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        println("Got main activity result: $requestCode, $resultCode, $data")

        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == AddImageDialog.PICK_IMAGE) {
                // Copy the given image to the local storage
                println("Pick image")
                val fileUri = data!!.data!!
                val uuid = UUID.randomUUID().toString()
                val imagesDir = File(filesDir, "images/").apply {
                    mkdirs()
                }

                val outFile = File(imagesDir, uuid)

                contentResolver.openInputStream(fileUri)!!.copyTo(outFile.outputStream())

                newNoteNavigationController.navigateNewNoteWithImage(findNavController(R.id.nav_host_fragment), uuid)
            }
            if (requestCode == AddImageDialog.TAKE_PHOTO) {
                println("Take photo")

                // Will always return true, unless app is restarted between images
                storageManager.getLastCameraImageUUID()?.let {
                    newNoteNavigationController.navigateNewNoteWithImage(findNavController(R.id.nav_host_fragment),
                        it
                    )
                }

                AddImageDialog.closeIfOpen()
            }
        } else {
            println("Result: failed")
        }
    }
}
