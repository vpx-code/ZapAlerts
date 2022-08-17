package com.xvlaze.zapalerts.model

import android.content.Context
import android.util.Log
import com.xvlaze.zapalerts.util.Extensions.isDateInThePast
import com.xvlaze.zapalerts.util.Extensions.toTimeStamp
import java.util.*

interface AlertType {
    val code: Int
    val interval: Long
    /*val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
        MyApplication.appContext,
        Random.nextInt(),
        Intent(MyApplication.appContext, AlertReceiver::class.java),
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            }
            else -> PendingIntent.FLAG_UPDATE_CURRENT
        }
    )*/

    fun getType(): Int
    fun calculateNextDate(): Long
    fun updateSavedDate(c: Context)
    fun getSavedDate(c: Context): Long
    fun hasDatePassed(c: Context): Boolean {
        val now = System.currentTimeMillis()
        val savedDate = getSavedDate(c)
        return if (!doesSavedDateExist(c)) true
        else {
            savedDate < now
        }
    }

    fun doesSavedDateExist(c: Context): Boolean = getSavedDate(c) != 0.toLong()
}

object Daily : AlertType {
    override val code = 0
    override val interval: Long = 86400000
    override fun getType(): Int = code
    override fun calculateNextDate(): Long {
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 9)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        if (calendar.isDateInThePast())
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        return calendar.timeInMillis
    }

    override fun updateSavedDate(c: Context) {
        val sharedPrefs = c.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putLong("nextDailyDate", calculateNextDate())
        Log.d(
            "ZAP_TAG",
            "Updating saved date. New date is ${calculateNextDate().toTimeStamp()}"
        )
        editor.apply()
    }

    override fun getSavedDate(c: Context): Long {
        val sharedPrefs = c.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        Log.d(
            "ZAP_TAG",
            "Getting saved date: ${sharedPrefs.getLong("nextDailyDate", 0)}"
        )
        return sharedPrefs.getLong("nextDailyDate", 0)
    }
}

object Weekly : AlertType {
    override val code = 1
    override val interval: Long = 86400000 * 7
    override fun getType(): Int = code
    override fun calculateNextDate(): Long {
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 9)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        if (calendar.isDateInThePast())
            calendar.add(Calendar.DAY_OF_MONTH, 7)
        return calendar.timeInMillis
    }

    override fun updateSavedDate(c: Context) {
        val sharedPrefs = c.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putLong("nextWeeklyDate", calculateNextDate())
        editor.apply()
    }

    override fun getSavedDate(c: Context): Long {
        val sharedPrefs = c.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        return sharedPrefs.getLong("nextWeeklyDate", 0)
    }
}

object Realtime : AlertType {
    override val code = 2
    override val interval: Long = 60000 // Ojo al cambiar esto. 5 minutos para realtime.
    override fun getType(): Int = code
    private var firstRingTime: Long = 0

    override fun calculateNextDate(): Long {
        val calendar: Calendar = Calendar.getInstance()
        //if (calendar.isDateInThePast()) {
        //    Log.d("ZAP_TAG", "Date in the past. Updating calendar to ${(interval / 60000).toInt()} minutes." )
        calendar.add(Calendar.MINUTE, (interval / 60000).toInt()) // Ojo al cambiar esto}
        //}
        return calendar.timeInMillis
    }

    override fun updateSavedDate(c: Context) {
        Log.d("ZAP_TAG", "Updating saved date to ${calculateNextDate()}")
        val sharedPrefs = c.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putLong("nextRealtimeDate", calculateNextDate())
        editor.apply()
    }

    override fun getSavedDate(c: Context): Long {
        val sharedPrefs = c.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        return sharedPrefs.getLong("nextRealtimeDate", 0)
    }

    fun getFirstRingTime(): Long = firstRingTime
    fun setFirstRingTine(time: Long) {
        firstRingTime = time
    }
}