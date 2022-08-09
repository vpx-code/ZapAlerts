package com.xvlaze.zapalerts.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.huawei.hms.searchkit.bean.NewsItem
import com.xvlaze.zapalerts.model.Interest
import com.xvlaze.zapalerts.model.OnNewsSearchPerformedCallback
import com.xvlaze.zapalerts.repository.Repository

class InterestDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(application.applicationContext)
    val foundInterest = MutableLiveData<Interest>()
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
        foundInterest.postValue(repository.searchInterest(name))
    }
}
