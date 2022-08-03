package com.xvlaze.zapalerts.model

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.content.ComponentName

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.xvlaze.zapalerts.receivers.AlertReceiver
import com.xvlaze.zapalerts.receivers.BootReceiver
import com.xvlaze.zapalerts.util.Extensions.toTimeStamp
import kotlin.random.Random

object MyAlarmManager {
    private var alarmManager: AlarmManager? = null

    /*fun scheduleAlarm(lastSavedDate: SharedPrefsDate, c: Context) {
        Log.d("ZAP_TAG", "Scheduling repeating alarm starting at ${lastSavedDate.getSavedDate(c).toTimeStamp()}")
        val pi = lastSavedDate.pendingIntent
        Log.d("ZAP_TAG", "Intent sender is: ${pi.intentSender}")
        getInstance(c).setRepeating(
            RTC_WAKEUP,
            lastSavedDate.getSavedDate(c),
            60000, // fixme lastSavedDate.interval,
            pi
        )
    }*/

    fun scheduleAlarm(lastSavedDate: AlertType, c: Context) {
        Log.d(
            "ZAP_TAG",
            "Scheduling repeating alarm starting at ${lastSavedDate.getSavedDate(c).toTimeStamp()}"
        )
        getInstance(c).setRepeating(
            RTC_WAKEUP,
            lastSavedDate.getSavedDate(c),
            lastSavedDate.interval,
            PendingIntent.getBroadcast(
                MyApplication.appContext,
                Random.nextInt(),
                Intent(MyApplication.appContext, AlertReceiver::class.java),
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    }
                    else -> PendingIntent.FLAG_UPDATE_CURRENT
                }
            )
        )
    }

    fun enableReceivers() {
        Log.d(
            "ZAP_TAG",
            "Enabled receivers!"
        )

        /*val bootReceiver = ComponentName(MyApplication.appContext, BootReceiver::class.java)

        MyApplication.appContext.packageManager.setComponentEnabledSetting(
            bootReceiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )*/

        val alertReceiver = ComponentName(MyApplication.appContext, AlertReceiver::class.java)

        MyApplication.appContext.packageManager.setComponentEnabledSetting(
            alertReceiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun disableReceiver() {
        val receiver = ComponentName(MyApplication.appContext, BootReceiver::class.java)

        MyApplication.appContext.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun getInstance(c: Context): AlarmManager {
        if (alarmManager == null) {
            alarmManager = c.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }
        return alarmManager!!
    }
}