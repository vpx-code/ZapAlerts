package com.xvlaze.zapalerts.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.xvlaze.zapalerts.model.IOnGetAllSuccessCallback
import com.xvlaze.zapalerts.model.InterestCloudObject
import com.xvlaze.zapalerts.repository.CloudDBRepository
import com.xvlaze.zapalerts.repository.Repository

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val cloudDBRepository = CloudDBRepository(application.applicationContext)
    private val repository = Repository(application.applicationContext)
    val savedInterests = MutableLiveData<MutableList<InterestCloudObject>>()

    // TODO: Tenemos que usar la base obligatoriamente? Creo que mejor tirar de caché excepto cuando hagamos C_UD.
    fun getSavedInterests() {
        cloudDBRepository.getAll(object : IOnGetAllSuccessCallback {
            override fun onSuccess(res: MutableList<InterestCloudObject>) {
                repository.saveLocalCopy(res)
                //InterestsManager().getFile() // TODO: Eliminar
                savedInterests.postValue(res)
            }
        })
    }
}
