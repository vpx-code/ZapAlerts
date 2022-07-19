package com.xvlaze.zapalerts.model

import android.content.Context

object SharedPrefsProvider {
    fun setOAuthToken(oauthToken: String, c: Context) {
        val sharedPrefs = c.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString("oauthToken", oauthToken)
        editor.apply()
    }

    fun getOAuthToken(c: Context): String? {
        val sharedPrefs = c.getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("oauthToken", null)
    }
}