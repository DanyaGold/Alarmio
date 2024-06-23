package com.example.alarmio.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.IBinder
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import com.example.alarmio.R
import com.example.alarmio.activities.RingActivity
import com.example.alarmio.application.App.Companion.CHANNEL_ID
import com.example.alarmio.broadcastreceiver.AlarmBroadcastReceiver.Companion.QRCODE
import com.example.alarmio.broadcastreceiver.AlarmBroadcastReceiver.Companion.MISSION
import com.example.alarmio.broadcastreceiver.AlarmBroadcastReceiver.Companion.TITLE


class AlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setLooping(true)

        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)

        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        mediaPlayer!!.setAudioAttributes(attributes)
        val defaultAlarmUri = android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI
        if (defaultAlarmUri != null) {
            try {
                mediaPlayer!!.setDataSource(this, defaultAlarmUri)
            } catch (e: Exception) {
                mediaPlayer!!.setDataSource(this, android.net.Uri.parse("android.resource://$packageName/${R.raw.alarm}"))
            }
        } else {
            mediaPlayer!!.setDataSource(this, android.net.Uri.parse("android.resource://$packageName/${R.raw.alarm}"))
        }
        mediaPlayer!!.setVolume(alarmVolume.toFloat(), alarmVolume.toFloat())
        mediaPlayer!!.prepare()
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, RingActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        notificationIntent.putExtra(QRCODE, intent.getStringExtra(QRCODE))
        notificationIntent.putExtra(TITLE, intent.getStringExtra(TITLE))
        notificationIntent.putExtra(MISSION, intent.getIntExtra(MISSION, -1))
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Alarmio")
            .setContentText("Нажмите, чтобы выключить будильник")
            .setSmallIcon(R.drawable.ic_alarm_black_24dp)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, true)
            .build()
        startForeground(1, notification)
        mediaPlayer!!.start()
        val pattern = longArrayOf(0, 100, 1000)
        vibrator!!.vibrate(pattern, 0)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer!!.stop()
        vibrator!!.cancel()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}