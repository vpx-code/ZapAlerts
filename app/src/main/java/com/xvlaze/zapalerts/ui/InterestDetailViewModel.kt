package com.xvlaze.zapalerts.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.huawei.hms.searchkit.bean.NewsItem
import com.xvlaze.zapalerts.model.Interest
import com.xvlaze.zapalerts.model.OnNewsSearchPerformedCallback
import com.xvlaze.zapalerts.repository.Repository
import com.xvlaze.zapalerts.util.Constants.AlertType.*

class InterestDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(application.applicationContext)
    val foundInterest = MutableLiveData<Interest>()
    val searchResults = MutableLiveData<ArrayList<NewsItem>>() // FIXME

    fun doSearch(searchQuery: String, type: Int, language: Int, country: Int) {
        when (type) {
            WEBSITE.id -> {
                TODO("doWebpageSearch")
            }
            IMAGE.id -> {
                TODO("doImageSearch")
            }
            VIDEO.id -> {
                TODO("doVideoSearch")
            }
            NEWS.id -> {
                repository.doNewsSearch(
                    searchQuery,
                    language,
                    country,
                    object: OnNewsSearchPerformedCallback {
                        override fun onNewsSearchResult(result: ArrayList<NewsItem>) {
                            searchResults.postValue(result)
                        }
                    }
                )
            }
        }
    }

    fun searchInterest(name: String) {
        foundInterest.postValue(repository.searchInterest(name))
    }
}
