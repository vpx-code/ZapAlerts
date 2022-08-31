package com.xvlaze.zapalerts.model

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type


class InterestsManager {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun getFile(): MutableList<InterestCloudObject>? {
        val reader = File(MyApplication.appContext.filesDir, "cache.json").bufferedReader()
        val typeToken: Type = object : TypeToken<MutableList<InterestCloudObject?>?>() {}.type
        return gson.fromJson<MutableList<InterestCloudObject>>(reader, typeToken)
    }

    fun saveLocalCopy(interests: MutableList<InterestCloudObject>) {
        val res = gson.toJson(interests)
        File(MyApplication.appContext.filesDir,"cache.json").writeText(res)
    }
}
