package com.xvlaze.zapalerts.model

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.xvlaze.zapalerts.util.Constants.AlertType
import com.xvlaze.zapalerts.util.Constants.InterestType

object InterestsManager {
    @RequiresApi(Build.VERSION_CODES.R)
    fun saveInterestToJSON(
        searchQuery: String,
        frequency: AlertType,
        language: Int,
        country: Int,
        type: InterestType,
        c: Context
    ) {
        JSONProvider.add(
            Interest(
                searchQuery,
                frequency,
                language,
                country,
                System.currentTimeMillis(),
                type
            )
        )
        JSONProvider.serialize(c)
    }

    fun getSavedInterests(c: Context): ArrayList<Interest> = JSONProvider.getSavedInterests(c)

    fun isInterestUnique(name: String, c: Context): Boolean =
        !getSavedInterests(c).any { it.name.lowercase() == name.lowercase() }

    fun updateInterestInJSON(interest: Interest, c: Context) {
        JSONProvider.update(interest)
        JSONProvider.serialize(c)
    }

    fun deleteInterestFromJSON(searchQuery: String, c: Context) {
        JSONProvider.remove(searchQuery)
        JSONProvider.serialize(c)
    }
}