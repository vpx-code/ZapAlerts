package com.xvlaze.zapalerts.util

object Constants {
    const val clientId = "106008821"
    const val clientSecret = "REDACTED_CLIENT_SECRET"
    private const val baseUrl = "https://oauth-login.cloud.huawei.com/"
    private const val subdomains = "oauth2/v3/token"
    const val fullUrl = "$baseUrl$subdomains"
    const val hostHeaderValue = "oauth-login.cloud.huawei.com"
    const val contentTypeHeaderValue = "application/x-www-form-urlencoded"
    const val maxElements = 10

    enum class InterestFrequency(val id: Int) {
        DAILY(0),
        WEEKLY(1),
        REALTIME(2)
    }
}