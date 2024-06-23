package com.example.alarmio.alarmslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmio.R
import com.example.alarmio.data.Alarm


class AlarmsListFragment : Fragment(), OnToggleAlarmListener {

    private lateinit var alarmRecyclerViewAdapter: AlarmRecyclerViewAdapter
    private lateinit var alarmsListViewModel: AlarmsListViewModel
    private lateinit var alarmsRecyclerView: RecyclerView
    private lateinit var addAlarm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alarmRecyclerViewAdapter = AlarmRecyclerViewAdapter(this)
        alarmsListViewModel = ViewModelProvider(this).get(AlarmsListViewModel::class.java)
        alarmsListViewModel.getAlarmsLiveData().observe(this, Observer { alarms ->
            alarms.let {
                alarmRecyclerViewAdapter.setAlarms(alarms)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_list_alarms, container, false)
        alarmsRecyclerView = view.findViewById(R.id.fragment_listalarms_recylerView)
        alarmsRecyclerView.layoutManager = LinearLayoutManager(context)
        alarmsRecyclerView.adapter = alarmRecyclerViewAdapter
        addAlarm = view.findViewById(R.id.fragment_listalarms_addAlarm)
        addAlarm.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_alarmsListFragment_to_createAlarmFragment)
        }
        return view
    }

    override fun onToggle(alarm: Alarm) {
        if (alarm.isStarted) {
            alarm.cancelAlarm(requireContext())
            alarmsListViewModel.update(alarm)
        } else {
            alarm.schedule(requireContext())
            alarmsListViewModel.update(alarm)
        }
    }

    override fun onDelete(alarm: Alarm) {
        if (alarm.isStarted) alarm.cancelAlarm(requireContext())
        alarmsListViewModel.delete(alarm.alarmId)
    }

    override fun onItemClick(alarm: Alarm, view: View) {
        val args = Bundle().apply {
            putSerializable(getString(R.string.arg_alarm_obj), alarm as java.io.Serializable)
        }
        Navigation.findNavController(view).navigate(R.id.action_alarmsListFragment_to_createAlarmFragment, args)
    }
}