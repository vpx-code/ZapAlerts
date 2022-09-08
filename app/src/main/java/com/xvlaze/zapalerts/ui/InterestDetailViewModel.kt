package com.xvlaze.zapalerts.ui

import android.app.Application
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

    fun doSearch(searchQuery: String, language: Int, country: Int) {
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
        cloudDBRepository.getByName(name, object: IOnGetByNameSuccessCallback {
            override fun onSuccess(res: InterestCloudObject) {
                foundInterest.postValue(res)
            }
        })
    }
}
