package com.xvlaze.zapalerts.model

import android.content.Context
import android.util.Log
import com.xvlaze.zapalerts.BuildConfig
import com.xvlaze.zapalerts.model.MyApplication.Companion.appContext
import com.xvlaze.zapalerts.util.Constants
import com.xvlaze.zapalerts.util.Extensions.isDateInThePast
import com.xvlaze.zapalerts.util.Extensions.toTimeStamp
import java.util.*

interface AlertType {
    val code: Int
    val interval: Long
    fun getType(): Int
    fun calculateNextDate(): Long
    fun getPreviouslySavedDate(): Long
    fun updateSavedDate()
    fun setSavedDate(date: Long)
    fun getSavedDate(): Long
    fun hasDatePassed(): Boolean {
        val now = System.currentTimeMillis()
        val savedDate = getSavedDate()
        return if (!doesSavedDateExist()) true
        else {
            savedDate < now
        }
    }

    fun doesSavedDateExist(): Boolean = getSavedDate() != 0.toLong()
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

    override fun getPreviouslySavedDate(): Long {
        val sharedPrefs = appContext.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        Log.d(
            "ZAP_TAG",
            "Getting previously saved date: ${sharedPrefs.getLong("lastDailyDate", 0)}"
        )
        return sharedPrefs.getLong("lastDailyDate", 0)
    }

    override fun updateSavedDate() {
        val sharedPrefs = appContext.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putLong("nextDailyDate", calculateNextDate())
        Log.d(
            "ZAP_TAG",
            "Updating saved date. New date is ${calculateNextDate().toTimeStamp()}"
        )
        editor.apply()
    }

    override fun setSavedDate(date: Long) {
        Log.d("ZAP_TAG", "Modifying Daily saved date to ${date.toTimeStamp()}")
        val sharedPrefs = appContext.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putLong("lastDailyDate", System.currentTimeMillis())
        editor.putLong("nextDailyDate", date)
        editor.apply()
    }

    override fun getSavedDate(): Long {
        val sharedPrefs = appContext.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val savedDate = sharedPrefs.getLong("nextDailyDate", System.currentTimeMillis())
        Log.d(
            "ZAP_TAG",
            "Getting saved date: ${savedDate.toTimeStamp()}"
        )
        return savedDate
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

    override fun getPreviouslySavedDate(): Long {
        val sharedPrefs = appContext.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        Log.d(
            "ZAP_TAG",
            "Getting previously saved date: ${sharedPrefs.getLong("lastWeeklyDate", 0)}"
        )
        return sharedPrefs.getLong("lastWeeklyDate", 0)
    }

    override fun updateSavedDate() {
        val sharedPrefs = appContext.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putLong("nextWeeklyDate", calculateNextDate())
        editor.apply()
    }

    override fun setSavedDate(date: Long) {
        Log.d("ZAP_TAG", "Modifying Weekly saved date to ${date.toTimeStamp()}")
        val sharedPrefs = appContext.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putLong("lastWeeklyDate", System.currentTimeMillis())
        editor.putLong("nextWeeklyDate", date)
        editor.apply()
    }

    override fun getSavedDate(): Long {
        val sharedPrefs = appContext.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        return sharedPrefs.getLong("nextWeeklyDate", 0)
    }
}

object Realtime : AlertType {
    override val code = 2
    override val interval: Long =
        if (BuildConfig.DEBUG && Constants.DEBUG) 60000 * 1 else 60000 * 5 // Ojo al cambiar esto. 5 minutos para realtime.

    override fun getType(): Int = code

    override fun getPreviouslySavedDate(): Long {
        val sharedPrefs = appContext.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        Log.d(
            "ZAP_TAG",
            "Getting previously saved date: ${sharedPrefs.getLong("lastRealtimeDate", 0)}"
        )
        return sharedPrefs.getLong("lastRealtimeDate", 0)
    }

    override fun calculateNextDate(): Long {
        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, (interval / 60000).toInt()) // Ojo al cambiar esto}
        return calendar.timeInMillis
    }

    override fun updateSavedDate() {
        val nextDate = calculateNextDate()
        Log.d("ZAP_TAG", "Updating Realtime saved date to ${nextDate.toTimeStamp()}")
        val sharedPrefs = appContext.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putLong("lastRealtimeDate", System.currentTimeMillis())
        editor.putLong("nextRealtimeDate", nextDate)
        editor.apply()
    }

    override fun setSavedDate(date: Long) {
        Log.d("ZAP_TAG", "Modifying Realtime saved date to ${date.toTimeStamp()}")
        val sharedPrefs = appContext.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putLong("lastRealtimeDate", System.currentTimeMillis())
        editor.putLong("nextRealtimeDate", date)
        editor.apply()
    }

    override fun getSavedDate(): Long {
        Log.d("ZAP_TAG", "Getting saved date...")
        val sharedPrefs = appContext.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        Log.d(
            "ZAP_TAG",
            "Saved date was ${sharedPrefs.getLong("nextRealtimeDate", 0).toTimeStamp()}"
        )
        return sharedPrefs.getLong("nextRealtimeDate", 0)
    }
}