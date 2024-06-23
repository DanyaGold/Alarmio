package com.example.alarmio.createalarm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.alarmio.data.Alarm
import com.example.alarmio.data.AlarmRepository


class CreateAlarmViewModel(application: Application) :
    AndroidViewModel(application) {
    private val alarmRepository: AlarmRepository

    init {
        alarmRepository = AlarmRepository(application)
    }

    fun insert(alarm: Alarm) {
        alarmRepository.insert(alarm)
    }
    fun update(alarm: Alarm) {
        alarmRepository.update(alarm)
    }
}