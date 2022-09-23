package com.xvlaze.zapalerts.model

import android.app.Application
import android.content.Context
import android.util.Log
import com.huawei.hms.searchkit.SearchKitInstance
import com.xvlaze.zapalerts.util.Constants.clientId

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        SearchKitInstance.init(this, clientId)
        OAuthTokenProvider.requestOAuthToken()

        CloudDB.initAGConnectCloudDB(this)
        cloudDB = CloudDB(this)
        cloudDB.createObjectType()
        cloudDB.openCloudDbZone()

        Log.d("ZAP_TAG", "Is first time.")

        Daily.updateSavedDate(this)
        MyAlarmManager.scheduleAlarm(Daily, this)

        Weekly.updateSavedDate(this)
        MyAlarmManager.scheduleAlarm(Weekly, this)

        Realtime.updateSavedDate(this)
        MyAlarmManager.scheduleAlarm(Realtime, this)

    }

    companion object {
        lateinit var appContext: Context
        lateinit var cloudDB: CloudDB
    }
}
