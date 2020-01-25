package pw.cub3d.cub3_notes.ui.settings


import android.content.Context
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

import pw.cub3d.cub3_notes.R
import pw.cub3d.cub3_notes.database.DataExporter
import java.io.File
import javax.inject.Inject


class SettingsFragment : Fragment() {

    @Inject
    lateinit var dataExporter: DataExporter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setting_export.setOnClickListener {
            GlobalScope.launch {
                dataExporter.exportToFile(
                    File(
                        "/sdcard/cub3d_notes_export_${ZonedDateTime.now().format(
                            DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        )}.json"
                    )
                )
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }
}
