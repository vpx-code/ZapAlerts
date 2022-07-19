package com.xvlaze.zapalerts.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

    class MyViewModelFactory(val app: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                MainViewModel(app) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}