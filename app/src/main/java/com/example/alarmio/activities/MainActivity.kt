package com.example.alarmio.activities

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.alarmio.R


class MainActivity : AppCompatActivity() {

    private var isShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toolbarButton: ImageButton = findViewById(R.id.toolbar_button)
        toolbarButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("О программе")
                .setMessage("Выполнили:\nстуденты группы 151112\nЛыбашев Даниил Николаевич\nРудаков Никита Николаевич")
                .setPositiveButton("OK", null)
                .show()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !getSystemService(AlarmManager::class.java).canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            startActivity(intent)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !getSystemService(NotificationManager::class.java).areNotificationsEnabled()) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 0)
            }
        }
    }

}