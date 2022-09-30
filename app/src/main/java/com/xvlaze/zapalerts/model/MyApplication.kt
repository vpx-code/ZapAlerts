package com.xvlaze.zapalerts.model

import android.app.Application
import android.content.Context
import com.huawei.hms.searchkit.SearchKitInstance
import com.newrelic.agent.android.NewRelic
import com.xvlaze.zapalerts.util.Constants.clientId

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

        NewRelic.withApplicationToken(
            "eu01xxba32e7845f4dbee7b85aef6d7a6bc17506e6-NRMA"
        ).start(appContext);

        SearchKitInstance.init(this, clientId)
        OAuthTokenProvider.requestOAuthToken()

        CloudDB.initAGConnectCloudDB(this)
        cloudDB = CloudDB(this)
        cloudDB.createObjectType()
        cloudDB.openCloudDbZone()

        if (SharedPrefsProvider.isFirstTime()) {
            Daily.updateSavedDate()
            Weekly.updateSavedDate()
            Realtime.updateSavedDate()
        }

        MyAlarmManager.scheduleAlarm(Daily, this)
        MyAlarmManager.scheduleAlarm(Weekly, this)
        MyAlarmManager.scheduleAlarm(Realtime, this)
    }

    companion object {
        lateinit var appContext: Context
        lateinit var cloudDB: CloudDB
    }
}
