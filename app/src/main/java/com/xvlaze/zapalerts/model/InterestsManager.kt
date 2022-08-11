package com.xvlaze.zapalerts.model

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

/*
TODO:
  - Abrir una branch para limpiar la mierda del repo de DB y unificarlo bien.
  - Pensar en si voy a usar una "caché" en JSON que se vaya actualizando cuando:
    - Guardes
    - Borres
    - Edites
    ...y no tengas que depender de la base de datos cada vez.
  - Reorganizar el código en consecuencia.
 */

object InterestsManager {
    @RequiresApi(Build.VERSION_CODES.R)
    fun saveInterestToJSON(
        searchQuery: String,
        frequency: Int,
        language: Int,
        country: Int,
        c: Context
    ) {
        JSONProvider.add(
            Interest(
                searchQuery,
                frequency,
                language,
                country,
                System.currentTimeMillis()
            )
        )
        JSONProvider.serialize(c)
    }

    fun getSavedInterests(c: Context): ArrayList<Interest> = JSONProvider.getSavedInterests(c)

    fun isInterestUnique(name: String, c: Context): Boolean =
        true//!getSavedInterests(c).any { it.name.lowercase() == name.lowercase() }

    fun isInterestUniqueInDB(name: String, c: Context): Boolean =
        !getSavedInterests(c).any { it.name.lowercase() == name.lowercase() }

    fun updateInterestInDB(interest: Interest, c: Context) {

    }

    fun deleteInterestFromDB(name: String, c: Context) {

    }

    fun updateInterestInJSON(interest: Interest, c: Context) {
        JSONProvider.update(interest)
        JSONProvider.serialize(c)
    }

    fun deleteInterestFromJSON(searchQuery: String, c: Context) {
        JSONProvider.remove(searchQuery)
        JSONProvider.serialize(c)
    }
}