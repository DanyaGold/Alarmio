package com.example.alarmio.activities

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil

import com.example.alarmio.R
import com.example.alarmio.broadcastreceiver.AlarmBroadcastReceiver.Companion.QRCODE
import com.example.alarmio.broadcastreceiver.AlarmBroadcastReceiver.Companion.MISSION
import com.example.alarmio.broadcastreceiver.AlarmBroadcastReceiver.Companion.TITLE
import com.example.alarmio.data.Alarm
import com.example.alarmio.databinding.ActivityRingBinding
import com.example.alarmio.service.AlarmService
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Random

class RingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRingBinding
    private val handler = Handler()
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_ring)
        binding.activity = this

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }


        binding.textViewTitle.text = intent.getStringExtra(TITLE)

        runnable = Runnable {
            val calendar = Calendar.getInstance()
            binding.textViewTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
            binding.textViewDate.text = SimpleDateFormat("d MMM EEEE", Locale.getDefault()).format(calendar.time)
            handler.postDelayed(runnable, 1000)
        }
        handler.post(runnable)

        animateClock()
    }

    fun onDismissClick() {
        when (intent.getIntExtra(MISSION, -1)) {
            1 -> {
                val qrcode = intent.getStringExtra(QRCODE)
                val intent = Intent(this, ScanActivity::class.java)
                intent.putExtra(ScanActivity.SCAN_MODE_KEY, ScanActivity.MODE_DISMISS_ALARM)
                intent.putExtra(QRCODE, qrcode)
                startActivity(intent)
            }

            2 -> {
                val intent = Intent(this, MathActivity::class.java)
                startActivity(intent)
            }

            3 -> {
                val intent = Intent(this, TextActivity::class.java)
                startActivity(intent)
            }

            0 -> {
                val intentService = Intent(applicationContext, AlarmService::class.java)
                applicationContext.stopService(intentService)
                finishAffinity()
            }
            else -> {
                val intentService = Intent(applicationContext, AlarmService::class.java)
                applicationContext.stopService(intentService)
                finishAffinity()
            }
        }
    }

    fun onSnoozeClick() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.add(Calendar.MINUTE, 10)
        val snoozeAlarm = Alarm(
            Random().nextInt(Int.MAX_VALUE),
            calendar[Calendar.HOUR_OF_DAY],
            calendar[Calendar.MINUTE],
            intent.getStringExtra(TITLE) ?: "",
            System.currentTimeMillis(),
            true,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            false,
            intent.getStringExtra(QRCODE),
            intent.getIntExtra(MISSION, -1)
        )
        snoozeAlarm.schedule(applicationContext)

        val intentService = Intent(applicationContext, AlarmService::class.java)
        applicationContext.stopService(intentService)
        finish()
    }

    private fun animateClock() {
        val rotateAnimation = ObjectAnimator.ofFloat(binding.activityRingClock, "rotation", 0f, 20f, 0f, -20f, 0f)
        rotateAnimation.repeatCount = ValueAnimator.INFINITE
        rotateAnimation.duration = 800
        rotateAnimation.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
    }
}
