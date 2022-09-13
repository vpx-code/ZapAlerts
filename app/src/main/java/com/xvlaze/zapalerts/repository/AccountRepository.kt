package com.xvlaze.zapalerts.repository

import android.content.Intent
import com.xvlaze.zapalerts.model.AccountModel
import com.xvlaze.zapalerts.model.IOnUserSignInCallback

class AccountRepository {
    fun signIn(data: Intent?, callback: IOnUserSignInCallback) = AccountModel.signIn(data, callback)
    fun signOut() = AccountModel.signOut()
}
