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
            query.trim(),
            language,
            country,
            c,
            callback
        )
    }

    fun updateSavedDate(
        frequency: Int
    ) {
        when (frequency) {
            DAILY.id -> {
                Daily.updateSavedDate()
            }
            WEEKLY.id -> {
                Weekly.updateSavedDate()
            }
            REALTIME.id -> {
                Realtime.updateSavedDate()
            }
        }
    }

    fun saveLocalCopy(interests: MutableList<InterestCloudObject>) = interestsManager.saveLocalCopy(interests)

    fun getSavedInterests(): MutableList<InterestCloudObject> = interestsManager.getFile() ?: mutableListOf()

    fun getSavedDate(
        frequency: Int
    ): Long? {
        return when (frequency) {
            DAILY.id -> {
                Daily.getSavedDate()
            }
            WEEKLY.id -> {
                Weekly.getSavedDate()
            }
            REALTIME.id -> {
                Realtime.getSavedDate()
            }
            else -> {
                null
            }
        }
    }
}
