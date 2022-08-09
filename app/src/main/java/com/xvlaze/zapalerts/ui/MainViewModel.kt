package com.xvlaze.zapalerts.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.xvlaze.zapalerts.model.InterestCloudObject
import com.xvlaze.zapalerts.repository.CloudDBRepository
import com.xvlaze.zapalerts.repository.InterestsRepository
import com.xvlaze.zapalerts.repository.Repository

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository(application.applicationContext)
    var cloudDBRepository = CloudDBRepository(application.applicationContext)
    private var interestsRepository: InterestsRepository
    val savedInterests: MutableLiveData<MutableList<InterestCloudObject>>

    init {
        cloudDBRepository.createObjectType()
        cloudDBRepository.openCloudDbZone()

        interestsRepository = InterestsRepository(cloudDBRepository.mCloudDbZone!!)

        savedInterests = interestsRepository.interestsList
    }

    fun getSavedInterests() {
        interestsRepository.getAll()
    }
}
