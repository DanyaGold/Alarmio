package com.example.alarmio.service

import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import com.example.alarmio.data.Alarm
import com.example.alarmio.data.AlarmRepository


class RescheduleAlarmsService : LifecycleService() {
    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val alarmRepository = AlarmRepository(application)
        alarmRepository.getAlarmsLiveData().observe(this, Observer<List<Alarm>> { alarms ->
            alarms.forEach { alarm ->
                if (alarm.isStarted) {
                    alarm.schedule(applicationContext)
                }
            }
        })
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }
}