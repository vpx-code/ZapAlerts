package com.xvlaze.zapalerts.repository

import android.content.Context
import com.xvlaze.zapalerts.model.*

class CloudDBRepository(c: Context) {
    private val cloudDBInstance = CloudDB(c)
    private var cloudDBQueries: CloudDBQueries

    init {
        cloudDBInstance.createObjectType()
        cloudDBInstance.openCloudDbZone()
        cloudDBQueries = CloudDBQueries(cloudDBInstance.mCloudDbZone!!)
    }

    fun getAll(callback: IOnGetAllSuccessCallback) = cloudDBQueries.getAll(callback)
    fun getByName(name: String, callback: IOnGetByNameSuccessCallback) = cloudDBQueries.getInterestByName(name, callback)
    fun isInterestUnique(name: String) = cloudDBQueries.isInterestUnique(name)
    fun save(interest: InterestCloudObject, callback: IOnSaveInterestSuccessCallback) = cloudDBQueries.saveInterest(interest, callback)
    fun edit(interest: InterestCloudObject) = cloudDBQueries.editInterest(interest)
    fun delete(interest: InterestCloudObject) = cloudDBQueries.deleteInterest(interest)
}