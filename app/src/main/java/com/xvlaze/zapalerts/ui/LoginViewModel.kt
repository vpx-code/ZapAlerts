package com.xvlaze.zapalerts.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.huawei.hms.support.hwid.HuaweiIdAuthManager
import com.xvlaze.zapalerts.model.IOnUserSignInCallback
import com.xvlaze.zapalerts.repository.AccountRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val accountRepository = AccountRepository()
    var isSignInSuccessful = MutableLiveData<Boolean>()

    fun signIn(data: Intent?) {
        accountRepository.signIn(data, object : IOnUserSignInCallback {
            override fun onSuccess(isSuccessful: Boolean) {
                isSignInSuccessful.postValue(isSuccessful)
            }
        })
    }
}