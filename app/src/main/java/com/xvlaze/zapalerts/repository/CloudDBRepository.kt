package com.xvlaze.zapalerts.repository

import android.content.Context
import com.xvlaze.zapalerts.model.CloudDB
import com.xvlaze.zapalerts.model.CloudDBQueries
import com.xvlaze.zapalerts.model.IOnSuccessListenerCallback
import com.xvlaze.zapalerts.model.InterestCloudObject

class CloudDBRepository(c: Context) {
    private val cloudDBInstance = CloudDB(c)
    private var cloudDBQueries: CloudDBQueries

    init {
        cloudDBInstance.createObjectType()
        cloudDBInstance.openCloudDbZone()
        cloudDBQueries = CloudDBQueries(cloudDBInstance.mCloudDbZone!!)
    }

    fun getAll(callback: IOnSuccessListenerCallback) = cloudDBQueries.getAll(callback)
    fun getByName(name: String) = cloudDBQueries.getInterestByName(name)
    fun isInterestUnique(name: String) = cloudDBQueries.isInterestUnique(name)
    fun save(interest: InterestCloudObject) = cloudDBQueries.saveInterest(interest)
    fun edit(interest: InterestCloudObject) = cloudDBQueries.editInterest(interest)
    fun delete(interest: InterestCloudObject) = cloudDBQueries.deleteInterest(interest)
}