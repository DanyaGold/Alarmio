package com.example.alarmio.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.util.concurrent.Executors


@Database(entities = [Alarm::class], version = 2, exportSchema = false)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao

    companion object {
        @Volatile
        private var INSTANCE: AlarmDatabase? = null
        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor = Executors.newFixedThreadPool(
            NUMBER_OF_THREADS
        )

        fun getDatabase(context: Context): AlarmDatabase? {
            if (INSTANCE == null) {
                synchronized(AlarmDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            AlarmDatabase::class.java,
                            "alarm_database"
                        ).build()
                    }
                }
            }
            return INSTANCE
        }
    }
}