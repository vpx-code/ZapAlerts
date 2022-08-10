package com.xvlaze.zapalerts.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.xvlaze.zapalerts.model.CloudDB
import com.xvlaze.zapalerts.model.CloudDBQueries
import com.xvlaze.zapalerts.model.IOnSuccessListenerCallback
import com.xvlaze.zapalerts.model.InterestCloudObject
import com.xvlaze.zapalerts.repository.CloudDBRepository
import com.xvlaze.zapalerts.repository.Repository

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(application.applicationContext)
    private val cloudDBInstance = CloudDB(application.applicationContext)
    private var cloudDBQueries: CloudDBQueries
    private val cloudDBRepository = CloudDBRepository(application.applicationContext)
    val savedInterests = MutableLiveData<MutableList<InterestCloudObject>>()

    init {
        cloudDBInstance.createObjectType()
        cloudDBInstance.openCloudDbZone()
        cloudDBQueries = CloudDBQueries(cloudDBInstance.mCloudDbZone!!)
    }

    fun getSavedInterests() {
        cloudDBRepository.getAll(object : IOnSuccessListenerCallback {
            override fun onSuccess(res: MutableList<InterestCloudObject>) {
                savedInterests.postValue(res)
            }
        })
    }
}
