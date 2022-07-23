package com.xvlaze.zapalerts.repository

import android.content.Context
import com.xvlaze.zapalerts.model.*
import com.xvlaze.zapalerts.util.Constants.InterestFrequency.*
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
        frequency: Int,
        language: Int,
        country: Int,
        type: Int
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

    fun overwriteInterest(
        searchQuery: String,
        frequency: Int,
        language: Int,
        country: Int,
        type: Int
    ) {
        InterestsManager.updateInterestInJSON(
            Interest(
                searchQuery,
                frequency,
                language,
                country,
                System.currentTimeMillis(),
                type
            ),
            c
        )
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

    fun getSavedInterests() = InterestsManager.getSavedInterests(c)

    fun searchInterest(name: String): Interest =
        InterestsManager.getSavedInterests(c).first { it.name == name }

    fun getPreferredLanguage(): Int = LanguageUtils.getPreferredLanguage()
    fun isInterestUnique(name: String): Boolean = InterestsManager.isInterestUnique(name, c)
    fun deleteInterest(searchQuery: String) = InterestsManager.deleteInterestFromJSON(searchQuery, c)
}
