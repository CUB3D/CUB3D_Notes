package pw.cub3d.cub3_notes.ui.dialog.reminderdialog

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentActivity
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener
import kotlinx.android.synthetic.main.dialog_reminder.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import pw.cub3d.cub3_notes.R
import java.util.*


class ReminderDialog(
    private val act: FragmentActivity,
    private val callback: (ZonedDateTime)->Unit
): Dialog(act) {

    val dateOptions = arrayOf(
        Pair("Today", ZonedDateTime.now()),
        Pair("Tomorrow", ZonedDateTime.now().plusDays(1)),
        Pair("Next week", ZonedDateTime.now().plusWeeks(1)),
        Pair("Custom", null)
    )


    var selectedDate: ZonedDateTime? = null

    private fun setAlarm(
        context: Context,
        time: Long,
        pendingIntent: PendingIntent
    ) {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (SDK_INT < Build.VERSION_CODES.KITKAT) alarmManager[AlarmManager.RTC_WAKEUP, time] =
            pendingIntent else if (Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M) alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        ) else if (SDK_INT >= Build.VERSION_CODES.M) alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent
        )
    }

    fun datePicker(callback: OnDateSetListener) {
        val now = Calendar.getInstance()
        val dpd: DatePickerDialog = DatePickerDialog.newInstance(
            callback,
            now[Calendar.YEAR],  // Initial year selection
            now[Calendar.MONTH],  // Initial month selection
            now[Calendar.DAY_OF_MONTH] // Inital day selection
        )
        dpd.show(act.supportFragmentManager, "DPD-Date")
    }

    fun timePicker(callback: com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener) {
        val now = ZonedDateTime.now()
        val tpd = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
            callback,
            now.hour,
            now.minute,
            now.second,
            true
        )
        tpd.show(act.supportFragmentManager, "TPD-Time")
    }

    fun simpleDialog() {
        datePicker(OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            timePicker(com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute, second ->
                // Month of year is 0 indexed
                val date = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
                val time = LocalTime.of(hourOfDay, minute, second)
                val zonedate = ZonedDateTime.of(date, time, ZoneId.systemDefault())

                println("Date selected: ${zonedate.format(DateTimeFormatter.ISO_ZONED_DATE_TIME)}")

                callback(zonedate)
            })
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.dialog_reminder)

        reminder_cancel.setOnClickListener { dismiss() }

        reminder_save.setOnClickListener {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

//            setAlarm(context, ZonedDateTime.now().plusSeconds(10).toEpochSecond(),
//                PendingIntent.getBroadcast(context, 0, Intent(context, ReminderBroadcastReciever::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT)
//            )
        }



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