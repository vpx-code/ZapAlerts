package com.xvlaze.zapalerts.model

import android.app.Application
import android.content.Context
import com.huawei.hms.searchkit.SearchKitInstance
import com.xvlaze.zapalerts.util.Constants.clientId

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        SearchKitInstance.init(this, clientId)
        OAuthTokenProvider.requestOAuthToken(this)
    }

    companion object {
        lateinit var appContext: Context
    }
}
