package com.xvlaze.zapalerts.model

import android.net.Uri

object User {
    var openId = ""
    var unionId = ""
    private var email = ""
    private var photoUri: Uri? = null
    private var photoUriString = ""
    private var displayName = ""

    fun reset() {
        openId = ""
        unionId = ""
        email = ""
        photoUri = null
        photoUriString = ""
        displayName = ""
    }
}