package com.xvlaze.zapalerts.repository

import android.content.Context
import com.xvlaze.zapalerts.model.*
import com.xvlaze.zapalerts.util.Constants.AlertType
import com.xvlaze.zapalerts.util.Constants.AlertType.*
import com.xvlaze.zapalerts.util.Constants.InterestType
import com.xvlaze.zapalerts.util.LanguageUtils

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

    fun doWebsiteSearch(
        query: String,
        language: Int,
        country: Int,
        callback: OnWebsiteSearchPerformedCallback
    ) {
        WebSearcher.search(
            query,
            language,
            country,
            c,
            callback
        )
    }

    fun doImageSearch(
        query: String,
        language: Int,
        country: Int,
        callback: OnImageSearchPerformedCallback
    ) {
        ImageSearcher.search(
            query,
            language,
            country,
            c,
            callback
        )
    }

    fun doVideoSearch(
        query: String,
        language: Int,
        country: Int,
        callback: OnVideoSearchPerformedCallback
    ) {
        VideoSearcher.search(
            query,
            language,
            country,
            c,
            callback
        )
    }

    fun saveInterest(
        searchQuery: String,
        frequency: AlertType,
        language: Int,
        country: Int,
        type: InterestType
    ) {
        InterestsManager.saveInterestToJSON(
            searchQuery,
            frequency,
            language,
            country,
            type,
            c
        )
        when (frequency) {
            DAILY -> {
                Daily.updateSavedDate(c)
                MyAlarmManager.scheduleAlarm(Daily, c)
            }
            WEEKLY -> {
                Weekly.updateSavedDate(c)
                MyAlarmManager.scheduleAlarm(Weekly, c)
            }
            REALTIME -> {
                Realtime.updateSavedDate(c)
                MyAlarmManager.scheduleAlarm(Realtime, c)
            }
        }
    }

    fun getSavedInterests() = InterestsManager.getSavedInterests(c)

    fun searchInterest(name: String): Interest =
        InterestsManager.getSavedInterests(c).filter { it.name == name }[0]

    fun getPreferredLanguage(): Int = LanguageUtils.getPreferredLanguage()
    fun isInterestUnique(name: String): Boolean = InterestsManager.isInterestUnique(name, c)
}
