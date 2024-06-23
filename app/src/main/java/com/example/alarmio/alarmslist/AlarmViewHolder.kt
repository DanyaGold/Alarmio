package com.example.alarmio.alarmslist

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.View
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmio.R
import com.example.alarmio.data.Alarm

class AlarmViewHolder(itemView: View, private val listener: OnToggleAlarmListener) :
    RecyclerView.ViewHolder(itemView) {
    private val alarmTime: TextView = itemView.findViewById(R.id.item_alarm_time)
    private val alarmRecurring: ImageView = itemView.findViewById(R.id.item_alarm_recurring)
    private val alarmRecurringDays: TextView = itemView.findViewById(R.id.item_alarm_recurringDays)
    private val alarmTitle: TextView = itemView.findViewById(R.id.item_alarm_title)
    var alarmStarted: Switch = itemView.findViewById(R.id.item_alarm_started)
    private val deleteAlarm: ImageView = itemView.findViewById(R.id.item_alarm_delete)
    private val alarmMission: TextView = itemView.findViewById(R.id.item_alarm_mission)


    fun bind(alarm: Alarm) {
        val alarmText = String.format("%02d:%02d", alarm.hour, alarm.minute)
        alarmTime.text = alarmText
        when (alarm.mission) {
            0 -> alarmMission.text = "Миссия: нет"
            1 -> alarmMission.text = "Миссия: QR код"
            2 -> alarmMission.text = "Миссия: математика"
            3 -> alarmMission.text = "Миссия: текст"
            else -> alarmMission.text = "Миссия: нет"
        }
        alarmStarted.isChecked = alarm.isStarted
        if (alarm.isRecurring) {
            alarmRecurring.setImageResource(R.drawable.ic_repeat_black_24dp)
            alarmRecurringDays.text = alarm.getRecurringDaysText()
        } else {
            alarmRecurring.setImageResource(R.drawable.ic_looks_one_black_24dp)
            alarmRecurringDays.text = "Однократно"
        }
        alarmTitle.text = alarm.title

        alarmStarted.setOnCheckedChangeListener { _, isChecked -> listener.onToggle(alarm) }

        deleteAlarm.setOnClickListener {
            showDeleteConfirmationDialog(alarm)
        }

        itemView.setOnClickListener { view ->
            listener.onItemClick(alarm, view)
        }


    }
    private fun showDeleteConfirmationDialog(alarm: Alarm) {
        val builder = AlertDialog.Builder(itemView.context)
        builder.setTitle("Удаление будильника")
            .setMessage("Вы уверены, что хотите удалить этот будильник?")
            .setPositiveButton("Удалить") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                listener.onDelete(alarm)
            }
            .setNegativeButton("Отмена") { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            .show()
    }
}