package pw.cub3d.cub3_notes.ui

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_main.*
import pw.cub3d.cub3_notes.core.manager.SettingsManager

object ToolbarController {
    fun setupToolbar(settingsManager: SettingsManager, fragment: Fragment, appBarLayout: AppBarLayout, toolbar: Toolbar, title: String) {
        (fragment.requireActivity() as MainActivity).apply {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = title

            setupActionBarWithNavController(
                fragment.findNavController(),
                appBarConfiguration
            )
        }

        settingsManager.toolbarEnabled.observe(fragment.viewLifecycleOwner, Observer {
            if(it) {
                appBarLayout.visibility = View.VISIBLE
            } else {
//                (fragment.requireActivity() as AppCompatActivity).setSupportActionBar(null)
                appBarLayout.visibility = View.GONE
            }
        })
    }

    fun setupSideNav(
        settingsManager: SettingsManager,
        fragment: Fragment
    ) {
        settingsManager.sideNavEnabled.observe(fragment.viewLifecycleOwner, Observer {
            if(it) {
                (fragment.requireActivity() as MainActivity).apply {
                    setupActionBarWithNavController(
                        fragment.findNavController(),
                        appBarConfiguration
                    )
                    nav_view.setupWithNavController(fragment.findNavController())

                    nav_view.menu.getItem(0).isChecked = true;

                    drawer_layout.setDrawerLockMode(
                        DrawerLayout.LOCK_MODE_UNLOCKED
                    )
                }

            } else {
                (fragment.requireActivity() as MainActivity).drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        })
    }
}