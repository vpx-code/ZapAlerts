package com.xvlaze.zapalerts.model

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

object JSONProvider {
    private lateinit var savedInterests: ArrayList<Interest>

    private val format = Json { prettyPrint = true }
    fun setup(c: Context) {
        val dir = c.filesDir.path + "/interests.json"

        if (Files.exists(Paths.get(dir))) {
            savedInterests = format.decodeFromString(File(dir).readText(Charsets.UTF_8))
        } else {
            savedInterests = ArrayList()
            val jsonString = format.encodeToString(savedInterests)
            File(dir).writeText(jsonString)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun serialize(c: Context) {
        val jsonString = format.encodeToString(savedInterests)
        File(c.filesDir.path + "/interests.json").writeText(jsonString)
    }

    fun add(interest: Interest) {
        savedInterests.add(interest)
    }

    fun update(interest: Interest) {
        val interestToUpdate = savedInterests.find {
            it.name == interest.name
        }
        val index = savedInterests.indexOf(interestToUpdate)
        savedInterests.remove(interestToUpdate)
        savedInterests.add(index, interest)
    }

    fun remove(interestName: String) {
        val interest = savedInterests.find {
            it.name == interestName
        }
        savedInterests.remove(interest)
    }

    fun getSavedInterests(c: Context): ArrayList<Interest> {
        return format.decodeFromString(File(c.filesDir.path + "/interests.json").readText(Charsets.UTF_8))
    }
}