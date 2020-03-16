package pw.cub3d.cub3_notes.ui.dialog.reminderdialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener
import kotlinx.android.synthetic.main.dialog_reminder.*
import org.threeten.bp.ZonedDateTime
import pw.cub3d.cub3_notes.R
import java.util.*


class ReminderDialog(private val act: FragmentActivity): Dialog(act) {

    val dateOptions = arrayOf(
        Pair("Today", ZonedDateTime.now()),
        Pair("Tomorrow", ZonedDateTime.now().plusDays(1)),
        Pair("Next week", ZonedDateTime.now().plusWeeks(1)),
        Pair("Custom", null)
    )


    var selectedDate: ZonedDateTime? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.dialog_reminder)

        reminder_cancel.setOnClickListener { dismiss() }



        val callback =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                println("Date selected: $year$monthOfYear$dayOfMonth")
                TODO("SET THE SELECTED DATE")
            }


        reminder_date.adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, dateOptions.map { it.first }.toTypedArray())

        reminder_date.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {


            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedDate = dateOptions[position].second

                if(selectedDate == null) {
                    val now = Calendar.getInstance()
                    val dpd: DatePickerDialog = DatePickerDialog.newInstance(
                        callback,
                        now[Calendar.YEAR],  // Initial year selection
                        now[Calendar.MONTH],  // Initial month selection
                        now[Calendar.DAY_OF_MONTH] // Inital day selection
                    )
                    dpd.show(act.supportFragmentManager, "DPD-Date")
                }
            }
        }
    }
}