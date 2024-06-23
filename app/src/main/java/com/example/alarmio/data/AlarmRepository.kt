package com.example.alarmio.data

import android.app.Application
import androidx.lifecycle.LiveData

class AlarmRepository(application: Application) {
    private val alarmDao: AlarmDao
    private val alarmsLiveData: LiveData<List<Alarm>>

    init {
        val db = AlarmDatabase.getDatabase(application)
        alarmDao = db!!.alarmDao()
        alarmsLiveData = alarmDao.getAlarms()
    }

    fun insert(alarm: Alarm) {
        AlarmDatabase.databaseWriteExecutor.execute { alarmDao.insert(alarm) }
    }

    fun update(alarm: Alarm) {
        AlarmDatabase.databaseWriteExecutor.execute { alarmDao.update(alarm) }
    }

    fun getAlarmsLiveData(): LiveData<List<Alarm>> {
        return alarmsLiveData
    }

    fun delete(alarmId: Int) {
        AlarmDatabase.databaseWriteExecutor.execute { alarmDao.delete(alarmId) }
    }
}