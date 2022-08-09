package com.xvlaze.zapalerts.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.huawei.hms.searchkit.bean.NewsItem
import com.xvlaze.zapalerts.model.InterestCloudObject
import com.xvlaze.zapalerts.model.OnNewsSearchPerformedCallback
import com.xvlaze.zapalerts.model.User
import com.xvlaze.zapalerts.repository.CloudDBRepository
import com.xvlaze.zapalerts.repository.InterestsRepository
import com.xvlaze.zapalerts.repository.Repository

class InterestsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(application.applicationContext)

    private val cloudDBRepository = CloudDBRepository(application.applicationContext)
    private var interestsRepository: InterestsRepository

    val searchResults = MutableLiveData<ArrayList<NewsItem>>() // FIXME
    val isInterestSaved = MutableLiveData<Boolean>()
    val interestSearchResult = MutableLiveData<InterestCloudObject>()
    val interestToEdit: MutableLiveData<InterestCloudObject>

    init {
        cloudDBRepository.createObjectType()
        cloudDBRepository.openCloudDbZone()
        interestsRepository = InterestsRepository(cloudDBRepository.mCloudDbZone!!)
        interestToEdit = interestsRepository.interestToEdit
    }

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
        interestsRepository.isInterestUnique(searchQuery)

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

        isInterestSaved.postValue(
            if (isInterestUnique(searchQuery)) {
                /*repository.saveInterest(
                    searchQuery,
                    frequency,
                    language,
                    country*/
                interestsRepository.saveInterest(
                    interestToSave
                )
                true
            } else {
                false
            }
        )
    }

    fun overwriteInterest(
        searchQuery: String,
        frequency: Int,
        language: Int,
        country: Int
    ) {
        // TODO: Buscar el método que tengo guardado en InterestsManager o JSONProvider para actualizar intereses.
        /*repository.overwriteInterest(
            searchQuery,
            frequency,
            language,
            country
        )
        isInterestSaved.postValue(true)*/
    }

    fun editInterest(interest: InterestCloudObject) {
        interestsRepository.editInterest(interest)
        isInterestSaved.postValue(true)
    }

    fun deleteInterest(interest: InterestCloudObject) {
        //repository.deleteInterest(searchQuery)
        interestsRepository.deleteInterest(interest)
    }

    fun getInterestInfo(interestName: String) {
        interestsRepository.getInterestByName(interestName)
    }
}