package com.xvlaze.zapalerts.model

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.xvlaze.zapalerts.util.Constants

object OAuthTokenProvider {

    fun requestOAuthToken(c: Context) {
        val oauthRequest : StringRequest = object : StringRequest( // TODO: Necesitaría crear un JSON Object en una data calss y convertirlo aquí. Eso significaría cambiarlo a JSONObjectREquest
            Method.POST,
            Constants.fullUrl,
            {
                MyRequestQueue.getInstance(c).cache.clear()
                val gson = Gson()
                val response: TokenResponse = gson.fromJson(it, TokenResponse::class.java)
                SharedPrefsProvider.setOAuthToken(response.access_token, c)
            },
            {
                SharedPrefsProvider.setOAuthToken("", c)
            })
        {
            override fun getParams(): MutableMap<String, String> {
                val parameters: MutableMap<String, String> = HashMap()
                parameters["grant_type"] = "client_credentials"
                parameters["client_id"] = Constants.clientId
                parameters["client_secret"] = Constants.clientSecret
                return parameters
            }

            override fun getHeaders(): MutableMap<String, String> {
                val headers: MutableMap<String, String> = HashMap()
                headers["Host"] = Constants.hostHeaderValue
                headers["Content-Type"] = Constants.contentTypeHeaderValue
                return headers
            }
        }

        MyRequestQueue.getInstance(c).add(oauthRequest)
    }

    fun getOAuthTokenFromSharedPrefs(c: Context) : String =
        SharedPrefsProvider.getOAuthToken(c) ?: ""
}