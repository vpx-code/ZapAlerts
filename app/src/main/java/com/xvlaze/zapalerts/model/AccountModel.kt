package com.xvlaze.zapalerts.model

import android.content.Intent
import com.huawei.agconnect.auth.AGConnectAuth
import com.huawei.agconnect.auth.HwIdAuthProvider
import com.huawei.hms.support.hwid.HuaweiIdAuthManager

object AccountModel {
    fun signIn(data: Intent?, callback: IOnUserSignInCallback) {
        val authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data)
        if (authHuaweiIdTask.isSuccessful) {
            val huaweiAccount = authHuaweiIdTask.result
            val accessToken = huaweiAccount.accessToken
            val credential = HwIdAuthProvider.credentialWithToken(accessToken)
            AGConnectAuth.getInstance().signIn(credential)
                .addOnSuccessListener { signInResult ->
                    val user = signInResult.user
                    User.unionId = user.uid
                    SharedPrefsProvider.setUserUid(user.uid)
                    callback.onSuccess(true)
                }.addOnFailureListener {
                    callback.onSuccess(false)
                }
        } else {
            callback.onSuccess(false)
        }
    }

    fun signOut() {
        AGConnectAuth.getInstance().signOut()
        User.reset()
        SharedPrefsProvider.deleteUserUid()
        InterestsManager().deleteFile()
    }
}

interface IOnUserSignInCallback {
    fun onSuccess(isSuccessful: Boolean)
}
