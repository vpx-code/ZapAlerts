package com.xvlaze.zapalerts.model

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

object MyRequestQueue {
    private var requestQueue: RequestQueue? = null

    fun getInstance(c: Context): RequestQueue {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(c)
        }
        return requestQueue!!
    }
}