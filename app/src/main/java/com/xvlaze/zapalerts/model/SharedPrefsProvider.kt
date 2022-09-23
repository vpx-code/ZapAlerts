package com.xvlaze.zapalerts.model

import android.content.Context
import android.content.SharedPreferences
import com.xvlaze.zapalerts.model.MyApplication.Companion.appContext
import com.xvlaze.zapalerts.util.Constants.InterestFrequency.*

object SharedPrefsProvider {
    private val sharedPrefs: SharedPreferences = appContext.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPrefs.edit()

    fun setOAuthToken(oauthToken: String) {
        val editor = sharedPrefs.edit()
        editor.putString("oauthToken", oauthToken)
        editor.apply()
    }

    fun getOAuthToken(): String? {
        return sharedPrefs.getString("oauthToken", null)
    }

    fun setNotFirstTime() {
        editor.putBoolean("isFirstTime", false)
        editor.apply()
    }

    fun isFirstTime(): Boolean {
        return sharedPrefs.getBoolean("isFirstTime", true)
    }

    fun isAlarmUnset(alertType: AlertType): Boolean {
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

    fun setUserUid(uid: String?) {
        editor.putString("userUid", uid)
        editor.apply()
    }

    fun getUserUid() : String = sharedPrefs.getString("userUid", "") ?: ""

    fun deleteUserUid() {
        editor.putString("userUid", "")
        editor.apply()
    }
}