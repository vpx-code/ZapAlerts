package com.xvlaze.zapalerts.util

import kotlinx.serialization.Serializable

object Constants {
    const val clientId = "106008821"
    const val clientSecret = "b2f267952be7cb779fdd20aae098f1a5276e1797e9f1f4f9234a0e5723fb2f5e"
    private const val baseUrl = "https://oauth-login.cloud.huawei.com/"
    private const val subdomains = "oauth2/v3/token"
    const val fullUrl = "$baseUrl$subdomains"
    const val hostHeaderValue = "oauth-login.cloud.huawei.com"
    const val contentTypeHeaderValue = "application/x-www-form-urlencoded"
    const val maxElements = 10

    @Serializable
    sealed class AlertType {
        @Serializable
        object DAILY : AlertType()
        @Serializable
        object WEEKLY : AlertType()
        @Serializable
        object REALTIME: AlertType()
    }

    @Serializable
    sealed class InterestType {
        @Serializable
        object News: InterestType()
        @Serializable
        object Website: InterestType()
        @Serializable
        object Image: InterestType()
        @Serializable
        object Video: InterestType()
    }
}