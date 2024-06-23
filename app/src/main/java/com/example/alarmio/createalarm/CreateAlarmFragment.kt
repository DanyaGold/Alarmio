package com.example.alarmio.createalarm

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.alarmio.R
import com.example.alarmio.data.Alarm
import com.example.alarmio.activities.ScanActivity
import java.util.Random


class CreateAlarmFragment : Fragment() {

    private lateinit var timePicker: TimePicker
    private lateinit var title: EditText
    private lateinit var scheduleAlarm: Button
    private lateinit var recurring: CheckBox
    private lateinit var mon: CheckBox
    private lateinit var tue: CheckBox
    private lateinit var wed: CheckBox
    private lateinit var thu: CheckBox
    private lateinit var fri: CheckBox
    private lateinit var sat: CheckBox
    private lateinit var sun: CheckBox
    private lateinit var recurringOptions: LinearLayout
    private lateinit var createAlarmViewModel: CreateAlarmViewModel
    private lateinit var missionSpinner: Spinner
    private var previousMissionSelection = 0

    private var alarm: Alarm? = null
    private var qrcode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createAlarmViewModel = ViewModelProvider(this).get(CreateAlarmViewModel::class.java)

        arguments?.let {
            alarm = it.getSerializable(getString(R.string.arg_alarm_obj)) as? Alarm
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_create_alarm, container, false)

        timePicker = view.findViewById(R.id.fragment_createalarm_timePicker)
        timePicker.setIs24HourView(true)
        title = view.findViewById(R.id.fragment_createalarm_title)
        scheduleAlarm = view.findViewById(R.id.fragment_createalarm_scheduleAlarm)
        recurring = view.findViewById(R.id.fragment_createalarm_recurring)
        mon = view.findViewById(R.id.fragment_createalarm_checkMon)
        tue = view.findViewById(R.id.fragment_createalarm_checkTue)
        wed = view.findViewById(R.id.fragment_createalarm_checkWed)
        thu = view.findViewById(R.id.fragment_createalarm_checkThu)
        fri = view.findViewById(R.id.fragment_createalarm_checkFri)
        sat = view.findViewById(R.id.fragment_createalarm_checkSat)
        sun = view.findViewById(R.id.fragment_createalarm_checkSun)
        recurringOptions = view.findViewById(R.id.fragment_createalarm_recurring_options)
        missionSpinner = view.findViewById(R.id.fragment_createalarm_mission)

        recurring.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                recurringOptions.visibility = View.VISIBLE
            } else {
                recurringOptions.visibility = View.GONE
            }
        }

        missionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                if (position == 1 && qrcode == null) { // Предполагая, что "QR Code" находится на позиции 1
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Добавить QR Код")
                    builder.setMessage("Для этой миссии нужно добавить QR код. Сканировать сейчас?")
                    builder.setPositiveButton("Да") { dialog, which ->
                        val addCodeIntent = Intent(requireContext(), ScanActivity::class.java)
                        addCodeIntent.putExtra(ScanActivity.SCAN_MODE_KEY, ScanActivity.MODE_SET_DISMISS_CODE)
                        startActivityForResult(addCodeIntent, REQUEST_CODE_QR_SCAN)
                    }
                    builder.setNegativeButton("Нет") { dialog, which ->
                        missionSpinner.setSelection(previousMissionSelection)
                        dialog.dismiss()
                    }
                    builder.setOnCancelListener {
                        missionSpinner.setSelection(previousMissionSelection)
                    }
                    builder.show()
                } else {
                    previousMissionSelection = position
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }


        scheduleAlarm.setOnClickListener { v ->
            if (recurring.isChecked && !mon.isChecked && !tue.isChecked && !wed.isChecked && !thu.isChecked && !fri.isChecked && !sat.isChecked && !sun.isChecked) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Ошибка!")
                    .setMessage("Вы не выбрали дни повтора")
                    .setPositiveButton("OK", null)
                    .show()
            } else {
                if (alarm != null) {
                    Toast.makeText(context, "Будильник обновлён", Toast.LENGTH_LONG).show()
                    updateAlarm()
                } else {
                    Toast.makeText(context, "Будильник установлен", Toast.LENGTH_LONG).show()
                    scheduleAlarm()
                }
                Navigation.findNavController(v)
                    .navigate(R.id.action_createAlarmFragment_to_alarmsListFragment)
            }
        }
        alarm?.let {
            updateAlarmInfo(it)
        }
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_QR_SCAN && resultCode == AppCompatActivity.RESULT_OK) {
            qrcode = data?.getStringExtra(ScanActivity.EXTRA_QR_CODE_HASH)
            Toast.makeText(requireContext(), "QR код добавлен", Toast.LENGTH_LONG).show()
        } else if (requestCode == REQUEST_CODE_QR_SCAN && resultCode == AppCompatActivity.RESULT_CANCELED) {
            missionSpinner.setSelection(previousMissionSelection)
        }
    }


    private fun scheduleAlarm() {
        val alarmId = Random().nextInt(Int.MAX_VALUE)
        val mission = missionSpinner.selectedItemPosition
        val alarm = Alarm(
            alarmId,
            TimePickerUtil.getTimePickerHour(timePicker),
            TimePickerUtil.getTimePickerMinute(timePicker),
            title.text.toString(),
            System.currentTimeMillis(),
            true,
            recurring.isChecked,
            mon.isChecked,
            tue.isChecked,
            wed.isChecked,
            thu.isChecked,
            fri.isChecked,
            sat.isChecked,
            sun.isChecked,
            qrcode,
            mission
        )
        createAlarmViewModel.insert(alarm)
        alarm.schedule(requireContext())
    }

    private fun updateAlarm() {
        val mission = missionSpinner.selectedItemPosition
        val updatedAlarm = Alarm(
            alarm!!.alarmId,
            TimePickerUtil.getTimePickerHour(timePicker),
            TimePickerUtil.getTimePickerMinute(timePicker),
            title.text.toString(),
            System.currentTimeMillis(),
            true,
            recurring.isChecked,
            mon.isChecked,
            tue.isChecked,
            wed.isChecked,
            thu.isChecked,
            fri.isChecked,
            sat.isChecked,
            sun.isChecked,
            qrcode,
            mission,
        )
        createAlarmViewModel.update(updatedAlarm)
        updatedAlarm.schedule(requireContext())
    }


    private fun updateAlarmInfo(alarm: Alarm) {
        title.setText(alarm.title)
        timePicker.hour = alarm.hour
        timePicker.minute = alarm.minute

        qrcode = alarm.qrcode
        missionSpinner.setSelection(alarm.mission)

        if (alarm.isRecurring) {
            recurring.isChecked = true
            recurringOptions.visibility = View.VISIBLE

            mon.isChecked = alarm.isMonday
            tue.isChecked = alarm.isTuesday
            wed.isChecked = alarm.isWednesday
            thu.isChecked = alarm.isThursday
            fri.isChecked = alarm.isFriday
            sat.isChecked = alarm.isSaturday
            sun.isChecked = alarm.isSunday
        }
    }

    companion object {
        private const val REQUEST_CODE_QR_SCAN = 1234
    }
}
