package com.xvlaze.zapalerts.model

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.huawei.hms.searchkit.SearchKitInstance
import com.xvlaze.zapalerts.receivers.BootReceiver
import com.xvlaze.zapalerts.util.Constants.clientId

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext



        // NO TOCAR
        SearchKitInstance.init(this, clientId)
        JSONProvider.setup(this)
        OAuthTokenProvider.requestOAuthToken(this)
    }

    companion object {
        lateinit var appContext: Context
    }
}
