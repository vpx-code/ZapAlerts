package com.xvlaze.zapalerts.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.huawei.hms.searchkit.bean.NewsItem
import com.xvlaze.zapalerts.model.Interest
import com.xvlaze.zapalerts.repository.Repository

class MainViewModel (application: Application): AndroidViewModel(application) {
    private val repository = Repository(application.applicationContext)
    val searchResults = MutableLiveData<ArrayList<NewsItem>>()
    val savedInterests = MutableLiveData<ArrayList<Interest>>()

    fun getSavedInterests() {
        savedInterests.postValue(repository.getSavedInterests())
    }

    /*fun doWebSearch(searchQuery: String) {
        repository.doWebSearch(searchQuery, object: IOnSearchPerformedCallback {
            override fun onWebSearchResult(result: ArrayList<WebItem>) { // FIXME: enum? Igual que ImageSaver
                searchResults.postValue(result)
            }
        })
    }*/
}