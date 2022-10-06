package com.xvlaze.zapalerts.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.huawei.hms.searchkit.bean.NewsItem
import com.xvlaze.zapalerts.model.IOnGetByNameSuccessCallback
import com.xvlaze.zapalerts.model.InterestCloudObject
import com.xvlaze.zapalerts.model.OnNewsSearchPerformedCallback
import com.xvlaze.zapalerts.repository.CloudDBRepository
import com.xvlaze.zapalerts.repository.Repository

class InterestDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(application.applicationContext)
    private val cloudDBRepository = CloudDBRepository()
    val foundInterest = MutableLiveData<InterestCloudObject>()
    val searchResults = MutableLiveData<ArrayList<NewsItem>>()
    val savedDate = MutableLiveData<Long?>()

    fun doSearch(searchQuery: String, language: Int, country: Int) {
        Log.d("ZAP_TAG", "ViewModel: Searching for Interest $searchQuery")
        repository.doNewsSearch(
            searchQuery,
            language,
            country,
            object : OnNewsSearchPerformedCallback {
                override fun onNewsSearchResult(result: ArrayList<NewsItem>) {
                    searchResults.postValue(result)
                }
            }
        )
    }

    fun searchInterest(name: String) {
        cloudDBRepository.getByName(name, object : IOnGetByNameSuccessCallback {
            override fun onSuccess(res: InterestCloudObject) {
                Log.d("ZAP_TAG", "Cloud object returned was $res.name")
                foundInterest.postValue(res)
            }
        })
    }

    fun getSavedDate(frequency: Int, fromNotification: Boolean) {
        savedDate.postValue(
            when {
                fromNotification -> {
                    repository.getSavedDate(frequency)
                }
                else -> null
            }
        )
    }
}
