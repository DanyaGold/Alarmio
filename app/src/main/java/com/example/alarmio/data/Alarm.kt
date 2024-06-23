package com.example.alarmio.data

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.alarmio.broadcastreceiver.AlarmBroadcastReceiver
import com.example.alarmio.broadcastreceiver.AlarmBroadcastReceiver.Companion.FRIDAY
import com.example.alarmio.broadcastreceiver.AlarmBroadcastReceiver.Companion.MISSION
import com.example.alarmio.broadcastreceiver.AlarmBroadcastReceiver.Companion.MONDAY
import com.example.alarmio.broadcastreceiver.AlarmBroadcastReceiver.Companion.QRCODE
import com.example.alarmio.broadcastreceiver.AlarmBroadcastReceiver.Companion.RECURRING
import com.example.alarmio.broadcastreceiver.AlarmBroadcastReceiver.Companion.SATURDAY
import com.example.alarmio.broadcastreceiver.AlarmBroadcastReceiver.Companion.SUNDAY
import com.example.alarmio.broadcastreceiver.AlarmBroadcastReceiver.Companion.THURSDAY
import com.example.alarmio.broadcastreceiver.AlarmBroadcastReceiver.Companion.TITLE
import com.example.alarmio.broadcastreceiver.AlarmBroadcastReceiver.Companion.TUESDAY
import com.example.alarmio.broadcastreceiver.AlarmBroadcastReceiver.Companion.WEDNESDAY
import java.io.Serializable
import java.util.Calendar

@Entity(tableName = "alarm_table")
class Alarm(
    @field:PrimaryKey var alarmId: Int,
    val hour: Int,
    val minute: Int,
    val title: String,
    var created: Long,
    var isStarted: Boolean,
    val isRecurring: Boolean,
    val isMonday: Boolean,
    val isTuesday: Boolean,
    val isWednesday: Boolean,
    val isThursday: Boolean,
    val isFriday: Boolean,
    val isSaturday: Boolean,
    val isSunday: Boolean,
    val qrcode: String? = null,
    val mission: Int = 0
):Serializable {

    fun schedule(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        intent.putExtra(RECURRING, isRecurring)
        intent.putExtra(MONDAY, isMonday)
        intent.putExtra(TUESDAY, isTuesday)
        intent.putExtra(WEDNESDAY, isWednesday)
        intent.putExtra(THURSDAY, isThursday)
        intent.putExtra(FRIDAY, isFriday)
        intent.putExtra(SATURDAY, isSaturday)
        intent.putExtra(SUNDAY, isSunday)
        intent.putExtra(TITLE, title)
        intent.putExtra(QRCODE, qrcode)
        intent.putExtra(MISSION, mission)

        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = minute
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar[Calendar.DAY_OF_MONTH] = calendar[Calendar.DAY_OF_MONTH] + 1
        }
        val alarmClockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, alarmPendingIntent)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()) {
            alarmManager.setAlarmClock(alarmClockInfo, alarmPendingIntent)
        } else {
            Toast.makeText(context, "Разрешение на установку точных будильников не получено", Toast.LENGTH_SHORT).show()
        }
        isStarted = true
    }

    fun cancelAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmBroadcastReceiver::class.java)
        val alarmPendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId, intent, PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(alarmPendingIntent)
        isStarted = false
    }

    fun getRecurringDaysText(): String? {
        if (!isRecurring) {
            return null
        }
        var days = ""
        if (isMonday) {
            days += "Пн "
        }
        if (isTuesday) {
            days += "Вт "
        }
        if (isWednesday) {
            days += "Ср "
        }
        if (isThursday) {
            days += "Чт "
        }
        if (isFriday) {
            days += "Пт "
        }
        if (isSaturday) {
            days += "Сб "
        }
        if (isSunday) {
            days += "Вс"
        }
        return days
    }
}