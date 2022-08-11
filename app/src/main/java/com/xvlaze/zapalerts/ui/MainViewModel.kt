package com.xvlaze.zapalerts.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.xvlaze.zapalerts.model.IOnSuccessListenerCallback
import com.xvlaze.zapalerts.model.InterestCloudObject
import com.xvlaze.zapalerts.repository.CloudDBRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val cloudDBRepository = CloudDBRepository(application.applicationContext)
    val savedInterests = MutableLiveData<MutableList<InterestCloudObject>>()

    fun getSavedInterests() {
        cloudDBRepository.getAll(object : IOnSuccessListenerCallback {
            override fun onSuccess(res: MutableList<InterestCloudObject>) {
                savedInterests.postValue(res)
            }
        })
    }
}
