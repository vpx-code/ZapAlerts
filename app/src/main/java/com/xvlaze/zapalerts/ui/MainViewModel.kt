package com.xvlaze.zapalerts.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.xvlaze.zapalerts.model.IOnGetAllSuccessCallback
import com.xvlaze.zapalerts.model.InterestCloudObject
import com.xvlaze.zapalerts.repository.CloudDBRepository
import com.xvlaze.zapalerts.repository.Repository

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val cloudDBRepository = CloudDBRepository()
    private val repository = Repository(application.applicationContext)
    val savedInterests = MutableLiveData<MutableList<InterestCloudObject>>()

    fun getSavedInterests() {
        val list = repository.getSavedInterests()
        if (list.isEmpty()) {
            Log.d("ZAP_TAG", "Local file list was empty. Fetching it from database...")
            getSavedInterestsFromDB()
            Log.d("ZAP_TAG", "Fetched interests list from database.")
        }
        else {
            Log.d("ZAP_TAG", "Fetched interests list from local file. List size is ${list.size}")
            savedInterests.postValue(list)
        }
    }

    fun getSavedInterestsFromDB() {
        cloudDBRepository.getAll(object : IOnGetAllSuccessCallback {
            override fun onSuccess(res: MutableList<InterestCloudObject>) {
                repository.saveLocalCopy(res)
                savedInterests.postValue(res)
            }
        })
    }
}
