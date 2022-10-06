package com.xvlaze.zapalerts.model

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.util.Log
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

        Log.d("ZAP_TAG", "Creating notification channel...")
        val channelID = "cyan"
        val name = "Cyan Notification Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(channelID, name, importance).apply {
            description = "Cyan's description"
            lightColor = Color.CYAN
            enableLights(true)
        }
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)

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
        lateinit var notificationManager: NotificationManager
    }
}
