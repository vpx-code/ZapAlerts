package com.xvlaze.zapalerts.model

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.xvlaze.zapalerts.receivers.AlertReceiver
import com.xvlaze.zapalerts.util.Extensions.toTimeStamp

object MyAlarmManager {
    private var alarmManager: AlarmManager? = null

    fun scheduleAlarm(lastSavedDate: AlertType, c: Context) {
        Log.d(
            "ZAP_TAG",
            "Scheduling repeating alarm starting at ${lastSavedDate.getSavedDate().toTimeStamp()}. TYPE = ${lastSavedDate.code}"
        )

        val intent = Intent(MyApplication.appContext, AlertReceiver::class.java)
        intent.putExtra("alarmFrequency", lastSavedDate.code)

        getInstance(c).setRepeating(
            RTC_WAKEUP,
            lastSavedDate.getSavedDate(),
            lastSavedDate.interval,
            PendingIntent.getBroadcast(
                MyApplication.appContext,
                lastSavedDate.code,
                intent,
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    }
                    else -> PendingIntent.FLAG_UPDATE_CURRENT
                }
            )
        )
    }

    private fun getInstance(c: Context): AlarmManager {
        if (alarmManager == null) {
            alarmManager = c.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }
        return alarmManager!!
    }
}