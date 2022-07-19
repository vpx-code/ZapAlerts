package com.xvlaze.zapalerts.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.huawei.hms.searchkit.bean.NewsItem
import com.xvlaze.zapalerts.model.Interest
import com.xvlaze.zapalerts.model.OnNewsSearchPerformedCallback
import com.xvlaze.zapalerts.repository.Repository
import com.xvlaze.zapalerts.util.Constants
import com.xvlaze.zapalerts.util.Constants.InterestType.*

class InterestDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(application.applicationContext)
    val foundInterest = MutableLiveData<Interest>()
    val searchResults = MutableLiveData<ArrayList<NewsItem>>() // FIXME

    fun doSearch(searchQuery: String, frequency: Constants.AlertType, type: Constants.InterestType, language: Int, country: Int) {
        when (type) {
            Website -> {
                TODO("doWebpageSearch")
            }
            Image -> {
                TODO("doImageSearch")
            }
            Video -> {
                TODO("doVideoSearch")
            }
            News -> {
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

    class MyViewModelFactory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(InterestDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                InterestDetailViewModel(app) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}
