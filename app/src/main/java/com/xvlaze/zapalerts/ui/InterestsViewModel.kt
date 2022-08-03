package com.xvlaze.zapalerts.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.huawei.hms.searchkit.bean.NewsItem
import com.xvlaze.zapalerts.model.Interest
import com.xvlaze.zapalerts.model.OnNewsSearchPerformedCallback
import com.xvlaze.zapalerts.repository.Repository

class InterestsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(application.applicationContext)
    val searchResults = MutableLiveData<ArrayList<NewsItem>>() // FIXME
    val isInterestSaved = MutableLiveData<Boolean>()
    val interestSearchResult = MutableLiveData<Interest>()
    private val preferredLanguage = MutableLiveData<Int>()

    fun doSearch(
        searchQuery: String,
        language: Int,
        country: Int
    ) {
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

    fun isInterestUnique(searchQuery: String): Boolean = repository.isInterestUnique(searchQuery)

    fun saveInterest(
        searchQuery: String,
        frequency: Int,
        language: Int,
        country: Int
    ) {
        isInterestSaved.postValue(
            if (isInterestUnique(searchQuery)) {
                repository.saveInterest(
                    searchQuery,
                    frequency,
                    language,
                    country
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

    fun overwriteInterest(
        searchQuery: String,
        frequency: Int,
        language: Int,
        country: Int
    ) {
        // TODO: Buscar el método que tengo guardado en InterestsManager o JSONProvider para actualizar intereses.
        repository.overwriteInterest(
            searchQuery,
            frequency,
            language,
            country
        )
        isInterestSaved.postValue(true)
    }

    fun deleteInterest(searchQuery: String) {
        repository.deleteInterest(searchQuery)
    }

    fun getInterestInfo(interestName: String) {
        interestSearchResult.postValue(repository.searchInterest(interestName))
    }

/*fun doWebSearch(searchQuery: String) {
    repository.doWebSearch(searchQuery, object: IOnSearchPerformedCallback {
        override fun onWebSearchResult(result: ArrayList<WebItem>) { // FIXME: enum? Igual que ImageSaver
            searchResults.postValue(result)
        }
    })
}*/
}