package com.xvlaze.zapalerts.repository

import android.content.Context
import com.xvlaze.zapalerts.model.CloudDB
import com.xvlaze.zapalerts.model.CloudDBQueries
import com.xvlaze.zapalerts.model.IOnSuccessListenerCallback
import com.xvlaze.zapalerts.model.InterestCloudObject

class CloudDBRepository(c: Context) {
    // FIXME: Aquí no se inicializa bien el objeto. Revisar porque si no no podemos usar esto correctamente.
    private val cloudDBQueries = CloudDB(c).mCloudDbZone?.let { CloudDBQueries(it) }

    fun getAll(callback: IOnSuccessListenerCallback) = cloudDBQueries?.getAll(callback)
    fun getByName(name: String) = cloudDBQueries?.getInterestByName(name)
    fun isInterestUnique(name: String) = cloudDBQueries?.isInterestUnique(name)
    fun save(interest: InterestCloudObject) = cloudDBQueries?.saveInterest(interest)
    fun edit(interest: InterestCloudObject) = cloudDBQueries?.editInterest(interest)
    fun delete(interest: InterestCloudObject) = cloudDBQueries?.deleteInterest(interest)
}