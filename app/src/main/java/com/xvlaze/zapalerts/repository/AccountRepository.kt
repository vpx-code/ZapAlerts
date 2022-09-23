package com.xvlaze.zapalerts.repository

import android.content.Intent
import com.xvlaze.zapalerts.model.AccountModel
import com.xvlaze.zapalerts.model.IOnUserSignInCallback
import com.xvlaze.zapalerts.model.SharedPrefsProvider

class AccountRepository {
    fun signIn(data: Intent?, callback: IOnUserSignInCallback) = AccountModel.signIn(data, callback)
    fun signOut() = AccountModel.signOut()
    fun isFirstTime(): Boolean = SharedPrefsProvider.isFirstTime()
    fun notFirstTimeAnymore() = SharedPrefsProvider.setNotFirstTime()
}
