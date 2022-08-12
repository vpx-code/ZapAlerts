package com.xvlaze.zapalerts.repository

import android.content.Context
import com.xvlaze.zapalerts.model.*
import com.xvlaze.zapalerts.util.Constants.InterestFrequency.*

class Repository(private val c: Context) {
    fun doNewsSearch(
        query: String,
        language: Int,
        country: Int,
        callback: OnNewsSearchPerformedCallback
    ) {
        NewsSearcher.search(
            query,
            language,
            country,
            c,
            callback
        )
    }

    fun saveInterest(
        frequency: Int,
    ) {
        when (frequency) {
            DAILY.id -> {
                Daily.updateSavedDate(c)
                MyAlarmManager.scheduleAlarm(Daily, c)
            }
            WEEKLY.id -> {
                Weekly.updateSavedDate(c)
                MyAlarmManager.scheduleAlarm(Weekly, c)
            }
            REALTIME.id -> {
                Realtime.updateSavedDate(c)
                MyAlarmManager.scheduleAlarm(Realtime, c)
            }
        }

        MyAlarmManager.enableReceivers()
    }

    fun overwriteInterest(
        frequency: Int
    ) {
        when (frequency) {
            DAILY.id -> {
                Daily.updateSavedDate(c)
                MyAlarmManager.scheduleAlarm(Daily, c)
            }
            WEEKLY.id -> {
                Weekly.updateSavedDate(c)
                MyAlarmManager.scheduleAlarm(Weekly, c)
            }
            REALTIME.id -> {
                Realtime.updateSavedDate(c)
                MyAlarmManager.scheduleAlarm(Realtime, c)
            }
        }
    }
}
