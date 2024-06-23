package com.example.alarmio.alarmslist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.alarmio.data.Alarm
import com.example.alarmio.data.AlarmRepository


class AlarmsListViewModel(application: Application) :
    AndroidViewModel(application) {
    private val alarmRepository: AlarmRepository
    private val alarmsLiveData: LiveData<List<Alarm>>

    init {
        alarmRepository = AlarmRepository(application)
        alarmsLiveData = alarmRepository.getAlarmsLiveData()
    }

    fun update(alarm: Alarm) {
        alarmRepository.update(alarm)
    }

    fun getAlarmsLiveData(): LiveData<List<Alarm>> {
        return alarmsLiveData
    }

    fun delete(alarmId: Int) {
        alarmRepository.delete(alarmId)
    }
}