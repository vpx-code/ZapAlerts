package com.xvlaze.zapalerts.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.huawei.hms.searchkit.bean.NewsItem
import com.xvlaze.zapalerts.model.*
import com.xvlaze.zapalerts.repository.CloudDBRepository
import com.xvlaze.zapalerts.repository.Repository

class InterestsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(application.applicationContext)
    private val cloudDBRepository = CloudDBRepository()

    val searchResults = MutableLiveData<ArrayList<NewsItem>>()
    val isInterestSaved = MutableLiveData<Boolean>()
    val isInterestUnique = MutableLiveData<Boolean>()
    val interestToEdit = MutableLiveData<InterestCloudObject>()

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

    fun isInterestUnique(searchQuery: String) {
        isInterestUnique.postValue(
            cloudDBRepository.isInterestUnique(
                searchQuery,
                repository.getSavedInterests()
            )
        )
    }

    fun saveInterest(
        searchQuery: String,
        frequency: Int,
        language: Int,
        country: Int
    ) {
        val interestToSave = InterestCloudObject()
        interestToSave.name = searchQuery.trim()
        interestToSave.frequency = frequency.toString()
        interestToSave.country = country.toString()
        interestToSave.language = language.toString()
        interestToSave.unionId = User.unionId

        cloudDBRepository.save(
            interestToSave,
            object : IOnSaveInterestSuccessCallback {
                override fun onSuccess(isCompleted: Boolean) {
                    // Saves all interests (including the added one) to a local copy.
                    cloudDBRepository.getAll(object : IOnGetAllSuccessCallback {
                        override fun onSuccess(res: MutableList<InterestCloudObject>) {
                            repository.saveLocalCopy(res)
                            isInterestSaved.postValue(true)
                        }
                    })
                }
            }
        )
    }

    fun editInterest(interest: InterestCloudObject) {
        cloudDBRepository.edit(interest)
        isInterestSaved.postValue(isInterestUnique(interest.name))
    }

    fun deleteInterest(interest: InterestCloudObject) {
        cloudDBRepository.delete(interest)
    }

    fun getInterestInfo(interestName: String) {
        cloudDBRepository.getByName(interestName, object : IOnGetByNameSuccessCallback {
            override fun onSuccess(res: InterestCloudObject) {
                interestToEdit.postValue(res)
            }
        })
    }
}