package com.xvlaze.zapalerts.model

import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.xvlaze.zapalerts.model.MyApplication.Companion.appContext

object MyRequestQueue {
    private var requestQueue: RequestQueue? = null

    fun getInstance(): RequestQueue {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(appContext)
        }
        return requestQueue!!
    }
}