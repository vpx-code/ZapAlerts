package com.xvlaze.zapalerts.model

import android.content.Context
import android.util.Log
import com.huawei.agconnect.AGCRoutePolicy
import com.huawei.agconnect.AGConnectInstance
import com.huawei.agconnect.AGConnectOptionsBuilder
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.cloud.database.AGConnectCloudDB
import com.huawei.agconnect.cloud.database.CloudDBZone
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException

class CloudDB(context: Context) {
    private var mCloudDb: AGConnectCloudDB = AGConnectCloudDB.getInstance(
        AGConnectInstance.buildInstance(
            AGConnectOptionsBuilder().setRoutePolicy(
                AGCRoutePolicy.GERMANY
            ).build(context)
        ), AGConnectAuth.getInstance()
    )
    var mCloudDbZone: CloudDBZone? = null

    companion object {
        fun initAGConnectCloudDB(context: Context?) {
            AGConnectCloudDB.initialize(context!!)
        }
    }

    fun createObjectType() {
        try {
            mCloudDb.createObjectType(ObjectTypeInfoHelper.getObjectTypeInfo())
        } catch (exception: AGConnectCloudDBException) {
            Log.w("CloudDbRepository", exception.errMsg)
        }
    }

    fun openCloudDbZone() {
        val mConfig = CloudDBZoneConfig(
            "base",
            CloudDBZoneConfig.CloudDBZoneSyncProperty.CLOUDDBZONE_CLOUD_CACHE,
            CloudDBZoneConfig.CloudDBZoneAccessProperty.CLOUDDBZONE_PUBLIC
        )
        mConfig.persistenceEnabled = true

        try {
            mCloudDbZone = mCloudDb.openCloudDBZone(mConfig, true)
        } catch (exception: AGConnectCloudDBException) {
            Log.w("CloudDbRepository", exception.errMsg)
        }
    }
}

interface IDatabase {
    fun getAll(callback: IOnGetAllSuccessCallback)
    fun isInterestUnique(name: String): Boolean
    fun saveInterest(interest: InterestCloudObject)
    fun editInterest(interest: InterestCloudObject)
    fun deleteInterest(interest: InterestCloudObject)
    fun getInterestByName(name: String, callback: IOnGetByNameSuccessCallback)
}