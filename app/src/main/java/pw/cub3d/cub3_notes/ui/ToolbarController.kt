package pw.cub3d.cub3_notes.ui

import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import pw.cub3d.cub3_notes.core.manager.SettingsManager

object ToolbarController {
    fun setupToolbarSettings(fragment: Fragment, settingsManager: SettingsManager, toolbar: Toolbar, appBarLayout: AppBarLayout) {
        settingsManager.toolbarEnabled.observe(fragment.viewLifecycleOwner, Observer {
            if (it) {
                toolbar.visibility = View.VISIBLE
                appBarLayout.visibility = View.VISIBLE
            } else {
                toolbar.visibility = View.GONE
                appBarLayout.visibility = View.GONE
            }
        })
    }

    fun setupToolbar(
        fragment: Fragment,
        toolbar: Toolbar,
        title: String
    ) {
        (fragment.requireActivity() as MainActivity).apply {
            setSupportActionBar(toolbar)
            supportActionBar!!.title = title

//            setupActionBarWithNavController(
//                fragment.findNavController(),
//                appBarConfiguration
//            )
        }
    }

    fun setupSideNav(
        settingsManager: SettingsManager,
        fragment: Fragment
    ) {
        settingsManager.sideNavEnabled.observe(fragment.viewLifecycleOwner, Observer {
            if (it) {
                (fragment.requireActivity() as MainActivity).apply {
                    setupActionBarWithNavController(
                        fragment.findNavController(),
                        appBarConfiguration
                    )
                    binding.navView.setupWithNavController(fragment.findNavController())

                    binding.navView.menu.getItem(0).isChecked = true

                    binding.drawerLayout.setDrawerLockMode(
                        DrawerLayout.LOCK_MODE_UNLOCKED
                    )
                }
            } else {
                (fragment.requireActivity() as MainActivity).binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        })
    }
}
