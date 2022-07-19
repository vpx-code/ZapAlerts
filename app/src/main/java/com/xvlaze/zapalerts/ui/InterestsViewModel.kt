package com.xvlaze.zapalerts.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.huawei.hms.searchkit.bean.NewsItem
import com.xvlaze.zapalerts.model.OnNewsSearchPerformedCallback
import com.xvlaze.zapalerts.repository.Repository
import com.xvlaze.zapalerts.util.Constants
import com.xvlaze.zapalerts.util.Constants.AlertType
import com.xvlaze.zapalerts.util.Constants.InterestType

class InterestsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(application.applicationContext)
    val searchResults = MutableLiveData<ArrayList<NewsItem>>() // FIXME
    val isInterestSaved = MutableLiveData<Boolean>()
    val preferredLanguage = MutableLiveData<Int>()

    fun doSearch(
        searchQuery: String,
        type: Int,
        language: Int,
        country: Int
    ) {
        when (type) {
            0 -> {
                TODO("doWebpageSearch")
            }
            1 -> {
                TODO("doImageSearch")
            }
            2 -> {
                TODO("doVideoSearch")
            }
            3 -> {
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
        }
    }

    fun isInterestUnique(searchQuery: String): Boolean = repository.isInterestUnique(searchQuery)

    fun saveInterest(
        searchQuery: String,
        frequency: AlertType,
        language: Int,
        country: Int,
        type: InterestType
    ) {
        isInterestSaved.postValue(
            if (isInterestUnique(searchQuery)) {
                repository.saveInterest(
                    searchQuery,
                    frequency,
                    language,
                    country,
                    type
                )
                true
            } else {
                false
            }
        )
    }

    fun getPreferredLanguage() {
        preferredLanguage.postValue(repository.getPreferredLanguage())
    }

    /*fun doWebSearch(searchQuery: String) {
        repository.doWebSearch(searchQuery, object: IOnSearchPerformedCallback {
            override fun onWebSearchResult(result: ArrayList<WebItem>) { // FIXME: enum? Igual que ImageSaver
                searchResults.postValue(result)
            }
        })
    }*/

    class MyViewModelFactory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(InterestsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                InterestsViewModel(app) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}