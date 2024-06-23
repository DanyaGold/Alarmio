package com.example.alarmio.alarmslist

import com.example.alarmio.data.Alarm
import android.view.View

interface OnToggleAlarmListener {
    fun onToggle(alarm: Alarm)
    fun onDelete(alarm: Alarm)
    fun onItemClick(alarm: Alarm, view: View)
}
