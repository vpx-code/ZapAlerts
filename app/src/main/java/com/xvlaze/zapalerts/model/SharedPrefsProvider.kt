package com.xvlaze.zapalerts.model

import android.content.Context
import com.xvlaze.zapalerts.util.Constants.InterestFrequency.*

object SharedPrefsProvider {
    fun setOAuthToken(oauthToken: String, c: Context) {
        val sharedPrefs = c.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString("oauthToken", oauthToken)
        editor.apply()
    }

    fun getOAuthToken(c: Context): String? {
        val sharedPrefs = c.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("oauthToken", null)
    }

    fun setNotFirstTime(c: Context) {
        val sharedPrefs = c.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putBoolean("isFirstTime", false)
        editor.apply()
    }

    fun isFirstTime(c: Context): Boolean {
        val sharedPrefs = c.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean("isFirstTime", true)
    }

    fun isAlarmUnset(c: Context, alertType: AlertType): Boolean {
        val sharedPrefs = c.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)

        return when (alertType.code) {
            DAILY.id -> {
                sharedPrefs.getBoolean("isDailySet", false)
            }
            WEEKLY.id -> {
                sharedPrefs.getBoolean("isWeeklySet", false)
            }
            REALTIME.id -> {
                sharedPrefs.getBoolean("isRealtimeSet", false)
            }
            else -> {
                false
            }
        }
    }
}