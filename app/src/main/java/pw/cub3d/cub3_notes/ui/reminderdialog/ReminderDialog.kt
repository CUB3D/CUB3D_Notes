package pw.cub3d.cub3_notes.ui.reminderdialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.dialog_reminder.*
import pw.cub3d.cub3_notes.R
import java.util.*

class ReminderDialog(ctx: Context): Dialog(ctx) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.dialog_reminder)

        reminder_cancel.setOnClickListener { dismiss() }


        val now = Calendar.getInstance()
        reminder_date.setOnClickListener {
//            DatePickerDialog.newInstance(DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
//
//            }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)).show(ownerActivity!!.fragmentManager.beginTransaction(), "DPD")
        }
    }
}