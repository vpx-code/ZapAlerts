package com.xvlaze.zapalerts.util

object Constants {
    const val DEBUG = false
    const val clientId = "106008821"
    const val clientSecret = "b2f267952be7cb779fdd20aae098f1a5276e1797e9f1f4f9234a0e5723fb2f5e"
    private const val baseUrl = "https://oauth-login.cloud.huawei.com/"
    private const val subdomains = "oauth2/v3/token"
    const val fullUrl = "$baseUrl$subdomains"
    const val hostHeaderValue = "oauth-login.cloud.huawei.com"
    const val contentTypeHeaderValue = "application/x-www-form-urlencoded"
    const val maxElements = 50

    enum class InterestFrequency(val id: Int) {
        DAILY(0),
        WEEKLY(1),
        REALTIME(2)
    }
}