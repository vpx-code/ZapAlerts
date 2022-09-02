package com.xvlaze.zapalerts.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.huawei.hms.searchkit.bean.NewsItem
import com.xvlaze.zapalerts.model.*
import com.xvlaze.zapalerts.repository.CloudDBRepository
import com.xvlaze.zapalerts.repository.Repository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class InterestsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(application.applicationContext)

    private val cloudDBRepository = CloudDBRepository(application.applicationContext)

    val searchResults = MutableLiveData<ArrayList<NewsItem>>()
    val isInterestSaved = MutableLiveData<Boolean>()
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

    fun isInterestUnique(searchQuery: String): Boolean =
        cloudDBRepository.isInterestUnique(searchQuery)

    fun saveInterest(
        searchQuery: String,
        frequency: Int,
        language: Int,
        country: Int
    ) {
        val interestToSave = InterestCloudObject()
        interestToSave.name = searchQuery
        interestToSave.frequency = frequency.toString()
        interestToSave.country = country.toString()
        interestToSave.language = language.toString()
        interestToSave.unionId = User.unionId

        runBlocking {
            val isInterestUnique: Deferred<Boolean> = async {
                when {
                    isInterestUnique(searchQuery) -> {
                        cloudDBRepository.save(
                            interestToSave
                        )
                        // Saves all interests (including the added one) to a local copy.
                        cloudDBRepository.getAll(object : IOnGetAllSuccessCallback {
                            override fun onSuccess(res: MutableList<InterestCloudObject>) {
                                repository.saveLocalCopy(res)
                            }
                        })
                        repository.updateSavedDate(frequency)
                        true
                    }
                    else -> {
                        false
                    }
                }
            }

            isInterestSaved.postValue(isInterestUnique.await())
        }
    }

    fun editInterest(interest: InterestCloudObject) {
        cloudDBRepository.edit(interest)
        repository.updateSavedDate(interest.frequency.toInt())
        isInterestSaved.postValue(true)
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