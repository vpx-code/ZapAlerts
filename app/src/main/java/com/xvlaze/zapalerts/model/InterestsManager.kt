package com.xvlaze.zapalerts.model

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.File
import java.lang.reflect.Type


class InterestsManager {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun getFile(): MutableList<InterestCloudObject>? {
        Log.d("ZAP_TAG", "Getting saved interests from local file...")
        val file = File(MyApplication.appContext.filesDir, "cache.json")
        if (!file.exists())
            file.createNewFile()
        val reader: BufferedReader = File(MyApplication.appContext.filesDir, "cache.json").bufferedReader()
        val typeToken: Type = object : TypeToken<MutableList<InterestCloudObject?>?>() {}.type
        return gson.fromJson<MutableList<InterestCloudObject>>(reader, typeToken)
    }

    fun saveLocalCopy(interests: MutableList<InterestCloudObject>) {
        val res = gson.toJson(interests)
        File(MyApplication.appContext.filesDir,"cache.json").writeText(res)
    }
}
