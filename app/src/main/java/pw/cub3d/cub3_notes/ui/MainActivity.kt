package pw.cub3d.cub3_notes.ui

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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import java.io.File
import java.util.*
import javax.inject.Inject
import kotlinx.android.synthetic.main.activity_main.*
import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.core.dagger.injector
import pw.cub3d.cub3_notes.core.database.dao.ImageDao
import pw.cub3d.cub3_notes.core.database.dao.VideoDao
import pw.cub3d.cub3_notes.core.database.entity.ImageEntry
import pw.cub3d.cub3_notes.core.database.entity.VideoEntry
import pw.cub3d.cub3_notes.core.manager.Layouts
import pw.cub3d.cub3_notes.core.manager.SettingsManager
import pw.cub3d.cub3_notes.core.manager.StorageManager
import pw.cub3d.cub3_notes.databinding.ActivityMainBinding
import pw.cub3d.cub3_notes.ui.dialog.addImage.AddImageDialog
import pw.cub3d.cub3_notes.ui.dialog.addVideo.AddVideoDialog
import pw.cub3d.cub3_notes.ui.nav.NewNoteNavigationController

class MainActivityViewModel @Inject constructor(
    val newNoteNavigationController: NewNoteNavigationController,
    val storageManager: StorageManager,
    val settingsManager: SettingsManager,
    private val imageDao: ImageDao,
    private val videoDao: VideoDao
) : ViewModel() {
    fun addImageToNote(lastNoteId: Long, uuid: String) {
        imageDao.insert(ImageEntry(noteId = lastNoteId, imageName = uuid))
    }

    fun addVideoToNote(lastNoteId: Long, it: String) {
        videoDao.insert(VideoEntry(noteId = lastNoteId, fileName = it))
    }
}

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var appBarConfiguration: AppBarConfiguration

    val viewModel: MainActivityViewModel by viewModels { injector.mainViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create notification channels
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel(NotificationChannel("default", "Defualt", NotificationManager.IMPORTANCE_LOW))
            }
        }

        viewModel.settingsManager.theme.observe(this, androidx.lifecycle.Observer {
            AppCompatDelegate.setDefaultNightMode(it.nightMode)

            obtainStyledAttributes(intArrayOf(R.attr.status_color)).apply {
                window.statusBarColor = this.getColor(0, Color.RED)
            }.recycle()
        })

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

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

            if (destination.id == R.id.nav_home) {
//                home_appBar.visibility = View.VISIBLE
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
//                home_appBar.visibility = View.GONE
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

        // Check if loaded due to notification click
        intent.getLongExtra("NOTE_ID", -1).takeIf { it > 0 }?.let {
            viewModel.newNoteNavigationController.editNote(navController, it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.main_changeLayout) {
            if (viewModel.settingsManager.noteLayout.value!! == Layouts.GRID) {
                viewModel.settingsManager.noteLayout.postValue(Layouts.LIST)
                item.icon = resources.getDrawable(R.drawable.ic_rows, theme)
            }
            if (viewModel.settingsManager.noteLayout.value!! == Layouts.LIST) {
                viewModel.settingsManager.noteLayout.postValue(Layouts.GRID)
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

        if (resultCode == Activity.RESULT_OK) {
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

                if (AddImageDialog.lastNoteId == null) {
                    viewModel.newNoteNavigationController.navigateNewNoteWithImage(
                        findNavController(
                            R.id.nav_host_fragment
                        ), uuid
                    )
                } else {
                    viewModel.addImageToNote(AddImageDialog.lastNoteId!!, uuid)
                }
            }
            if (requestCode == AddImageDialog.TAKE_PHOTO) {
                println("Take photo")

                // Will always return true, unless app is restarted between images
                viewModel.storageManager.getLastCameraImageUUID()?.let {
                    if (AddImageDialog.lastNoteId == null) {
                        viewModel.newNoteNavigationController.navigateNewNoteWithImage(
                            findNavController(R.id.nav_host_fragment),
                            it
                        )
                    } else {
                        viewModel.addImageToNote(AddImageDialog.lastNoteId!!, it)
                    }
                }

                AddImageDialog.closeIfOpen()
            }
            if (requestCode == AddVideoDialog.PICK_VIDEO) {
                // Copy the given video to the local storage
                println("Pick video")
                val fileUri = data!!.data!!
                val uuid = UUID.randomUUID().toString()
                val imagesDir = File(filesDir, "video/").apply {
                    mkdirs()
                }

                val outFile = File(imagesDir, uuid)

                contentResolver.openInputStream(fileUri)!!.copyTo(outFile.outputStream())

                if (AddVideoDialog.lastNoteId == null) {
                    viewModel.newNoteNavigationController.navigateNewNoteWithVideo(
                        findNavController(
                            R.id.nav_host_fragment
                        ), uuid
                    )
                } else {
                    viewModel.addVideoToNote(AddVideoDialog.lastNoteId!!, uuid)
                }
            }
            if (requestCode == AddVideoDialog.TAKE_VIDEO) {
                println("Take video")

                // Will always return true, unless app is restarted between images
                viewModel.storageManager.getLastCameraVideoUUID()?.let {
                    if (AddVideoDialog.lastNoteId == null) {
                        viewModel.newNoteNavigationController.navigateNewNoteWithVideo(
                            findNavController(R.id.nav_host_fragment),
                            it
                        )
                    } else {
                        viewModel.addVideoToNote(AddVideoDialog.lastNoteId!!, it)
                    }
                }

                AddVideoDialog.closeIfOpen()
            }
        } else {
            println("Result: failed")
        }
    }
}
