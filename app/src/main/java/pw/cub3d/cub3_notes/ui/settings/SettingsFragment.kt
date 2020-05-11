package pw.cub3d.cub3_notes.ui.settings


import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.coroutinespermission.PermissionManager
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import pw.cub3d.cub3_notes.core.dagger.injector
import pw.cub3d.cub3_notes.core.manager.Themes
import pw.cub3d.cub3_notes.databinding.FragmentSettingsBinding
import java.io.File


class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsViewModel by viewModels { injector.settingsViewModelFactory() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        FragmentSettingsBinding.inflate(inflater, container, false)
        .apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setting_export.setOnClickListener {
            GlobalScope.launch {
                val permissionRequest = PermissionManager.requestPermissions(
                    this@SettingsFragment,
                    1,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )

                when (permissionRequest) {
                    is PermissionResult.PermissionGranted -> {
                        viewModel.dataExporter.exportToFile(
                            File(
                                "/sdcard/notes_export_${ZonedDateTime.now().format(
                                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
                                )}.json"
                            )
                        )
                    }
                    is PermissionResult.PermissionDenied -> {
                        requireActivity().runOnUiThread {
                            Toast.makeText(
                                requireContext(),
                                "Storage permission needed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    is PermissionResult.ShowRational -> {
                        requireActivity().runOnUiThread { Toast.makeText(requireContext(), "Storage permission needed", Toast.LENGTH_LONG).show()}
                    }
                    is PermissionResult.PermissionDeniedPermanently -> {
                        requireActivity().runOnUiThread {Toast.makeText(requireContext(), "Storage permission needed", Toast.LENGTH_LONG).show()}
                    }
                }
            }
        }

        settings_test_sync.setOnClickListener {
            viewModel.openTasksSyncManager.test()
        }
        settings_test_sync.visibility = View.GONE

        // Theme settings
        binding.settingsThemeSystem.setOnClickListener { viewModel.settingsManager.setTheme(Themes.SYSTEM) }
        binding.settingsThemeLight.setOnClickListener { viewModel.settingsManager.setTheme(Themes.LIGHT) }
        binding.settingsThemeDark.setOnClickListener { viewModel.settingsManager.setTheme(Themes.DARK) }

        viewModel.settingsManager.theme.observe(viewLifecycleOwner, Observer { theme ->
            when (theme) {
                Themes.SYSTEM -> binding.settingsThemeSystem.isChecked = true
                Themes.LIGHT -> binding.settingsThemeLight.isChecked = true
                Themes.DARK -> binding.settingsThemeDark.isChecked = true
            }
        })

        // Sidenav settings
        binding.settingsSitenavOn.setOnClickListener { viewModel.settingsManager.setSideNavEnabled(true) }
        binding.settingsSidenavOff.setOnClickListener { viewModel.settingsManager.setSideNavEnabled(false) }
        viewModel.settingsManager.sideNavEnabled.observe(viewLifecycleOwner, Observer {
            when (it) {
                true -> binding.settingsSitenavOn.isChecked = true
                false -> binding.settingsSidenavOff.isChecked = true
            }
        })

        // Toolbar settings
        binding.settingsToolbarOn.setOnClickListener { viewModel.settingsManager.setToolbarEnabled(true) }
        binding.settingsToolbarOff.setOnClickListener { viewModel.settingsManager.setToolbarEnabled(false) }
        viewModel.settingsManager.toolbarEnabled.observe(viewLifecycleOwner, Observer {
            when (it) {
                true -> binding.settingsToolbarOn.isChecked = true
                false -> binding.settingsToolbarOff.isChecked = true
            }
        })

        // Quicknote
        binding.settingsQuicknoteOn.setOnClickListener { viewModel.settingsManager.setQuickNoteEnabled(true) }
        binding.settingsQuicnoteOff.setOnClickListener { viewModel.settingsManager.setQuickNoteEnabled(false) }
        viewModel.settingsManager.quickNoteEnabled.observe(viewLifecycleOwner, Observer {
            when (it) {
                true -> binding.settingsQuicknoteOn.isChecked = true
                false -> binding.settingsQuicnoteOff.isChecked = true
            }
        })
    }
}
