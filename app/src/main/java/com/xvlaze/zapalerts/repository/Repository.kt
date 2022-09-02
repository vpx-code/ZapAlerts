package com.xvlaze.zapalerts.repository

import android.content.Context
import com.xvlaze.zapalerts.model.*
import com.xvlaze.zapalerts.util.Constants.InterestFrequency.*

class Repository(private val c: Context) {
    private val interestsManager = InterestsManager()

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

    fun updateSavedDate(
        frequency: Int,
    ) {
        when (frequency) {
            DAILY.id -> {
                Daily.updateSavedDate(c)
                /*if (SharedPrefsProvider.isAlarmUnset(c, Daily))
                    MyAlarmManager.scheduleAlarm(Daily, c)*/
            }
            WEEKLY.id -> {
                Weekly.updateSavedDate(c)
                /*if (SharedPrefsProvider.isAlarmUnset(c, Weekly))
                    MyAlarmManager.scheduleAlarm(Weekly, c)*/
            }
            REALTIME.id -> {
                Realtime.updateSavedDate(c)
                /*if (SharedPrefsProvider.isAlarmUnset(c, Realtime))
                    MyAlarmManager.scheduleAlarm(Realtime, c)*/
            }
        }
    }

    /*fun saveInterestNew(interestToSave: InterestCloudObject) {
        saveInterest(interestToSave.frequency.toInt())
        interestsManager.saveInterest(interestToSave)
    }*/

    fun saveLocalCopy(interests: MutableList<InterestCloudObject>) = interestsManager.saveLocalCopy(interests)

    fun getSavedInterests(): MutableList<InterestCloudObject> = interestsManager.getFile() ?: mutableListOf()
}
