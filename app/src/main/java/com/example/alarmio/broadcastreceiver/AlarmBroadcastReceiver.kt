package com.example.alarmio.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.alarmio.service.AlarmService
import com.example.alarmio.service.RescheduleAlarmsService
import java.util.Calendar

class AlarmBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
                startRescheduleAlarmsService(context)
        } else {
            if (!intent.getBooleanExtra(RECURRING, false)) {
                startAlarmService(context, intent)
            } else {
                if (alarmIsToday(intent)) {
                    startAlarmService(context, intent)
                }
                startRescheduleAlarmsService(context)
            }
        }
    }

    private fun alarmIsToday(intent: Intent): Boolean {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val today = calendar.get(Calendar.DAY_OF_WEEK)

        return when (today) {
            Calendar.MONDAY -> intent.getBooleanExtra(MONDAY, false)
            Calendar.TUESDAY -> intent.getBooleanExtra(TUESDAY, false)
            Calendar.WEDNESDAY -> intent.getBooleanExtra(WEDNESDAY, false)
            Calendar.THURSDAY -> intent.getBooleanExtra(THURSDAY, false)
            Calendar.FRIDAY -> intent.getBooleanExtra(FRIDAY, false)
            Calendar.SATURDAY -> intent.getBooleanExtra(SATURDAY, false)
            Calendar.SUNDAY -> intent.getBooleanExtra(SUNDAY, false)
            else -> false
        }
    }

    private fun startAlarmService(context: Context, intent: Intent) {
        val intentService = Intent(context, AlarmService::class.java)
        intentService.putExtra(TITLE, intent.getStringExtra(TITLE))
        intentService.putExtra(QRCODE, intent.getStringExtra(QRCODE))
        intentService.putExtra(MISSION, intent.getIntExtra(MISSION, -1))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService)
        } else {
            context.startService(intentService)
        }
    }

    private fun startRescheduleAlarmsService(context: Context) {
        val intentService = Intent(context, RescheduleAlarmsService::class.java)
        context.startService(intentService)
    }

    companion object {
        const val MONDAY = "MONDAY"
        const val TUESDAY = "TUESDAY"
        const val WEDNESDAY = "WEDNESDAY"
        const val THURSDAY = "THURSDAY"
        const val FRIDAY = "FRIDAY"
        const val SATURDAY = "SATURDAY"
        const val SUNDAY = "SUNDAY"
        const val RECURRING = "RECURRING"
        const val TITLE = "TITLE"
        const val QRCODE = "QRCODE"
        const val MISSION = "MISSION"
    }
}