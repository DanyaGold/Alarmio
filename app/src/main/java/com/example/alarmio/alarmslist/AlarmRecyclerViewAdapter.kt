package com.example.alarmio.alarmslist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmio.R
import com.example.alarmio.data.Alarm


class AlarmRecyclerViewAdapter(listener: OnToggleAlarmListener) :
    RecyclerView.Adapter<AlarmViewHolder>() {
    private var alarms: List<Alarm>
    private val listener: OnToggleAlarmListener

    init {
        alarms = ArrayList()
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm: Alarm = alarms[position]
        holder.bind(alarm)
    }

    override fun getItemCount(): Int {
        return alarms.size
    }

    fun setAlarms(alarms: List<Alarm>) {
        this.alarms = alarms
        notifyDataSetChanged()
    }

    override fun onViewRecycled(holder: AlarmViewHolder) {
        super.onViewRecycled(holder)
        holder.alarmStarted.setOnCheckedChangeListener(null)
    }
}
