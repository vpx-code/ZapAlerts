package com.xvlaze.zapalerts.model

import android.content.Context
import android.util.Log
import com.huawei.hms.searchkit.SearchKitInstance
import com.huawei.hms.searchkit.bean.CommonSearchRequest
import com.huawei.hms.searchkit.bean.NewsItem
import com.xvlaze.zapalerts.util.Constants
import com.xvlaze.zapalerts.util.LanguageUtils.getLanguageFromDropdownSelection
import com.xvlaze.zapalerts.util.LanguageUtils.getRegionFromDropdownSelection

interface OnNewsSearchPerformedCallback {
    fun onNewsSearchResult(result: ArrayList<NewsItem>)
}

abstract class Searchable<in T> {
    abstract fun search(
        query: String,
        language: Int,
        country: Int,
        c: Context,
        callback: T
    )
}

// FIXME: Si buscas "Lukoil" se rompe. Estamos manejando correctamente los resultados cuando son nulos?
object NewsSearcher : Searchable<OnNewsSearchPerformedCallback>() {
    override fun search(
        query: String,
        language: Int,
        country: Int,
        c: Context,
        callback: OnNewsSearchPerformedCallback
    ) {
        val commonSearchRequest = CommonSearchRequest()
        commonSearchRequest.setQ(query)
        commonSearchRequest.setLang(
            getLanguageFromDropdownSelection(language)
        )
        commonSearchRequest.setSregion(
            getRegionFromDropdownSelection(country)
        )
        commonSearchRequest.setPs(Constants.maxElements)
        commonSearchRequest.setPn(1)
        val searchKitInstance = SearchKitInstance.getInstance()
        val token = OAuthTokenProvider.getOAuthTokenFromSharedPrefs(c)
        SearchKitInstance.instance.setInstanceCredential(token)
        val newsSearchResponse = searchKitInstance.newsSearcher.search(commonSearchRequest)
        var results = arrayListOf<NewsItem>()
        // FIXME: A veces se rompe, ocurre cuando aprietas una notificación. Vigilar back stack?
        if (newsSearchResponse != null) {
            if (newsSearchResponse.getData().isNotEmpty()) {
                results = newsSearchResponse.getData() as ArrayList<NewsItem>
                results.apply {
                    distinctBy { it.title }
                    distinctBy { it.clickUrl }
                    sortByDescending { it.publishTime }
                }
            }
            else {
                Log.d("ZAP_TAG", "Search response was empty.")
            }
        }
        else {
            Log.d("ZAP_TAG", "Search response was null. Weird thing! Let's pretend nothing happened...")
        }
        callback.onNewsSearchResult(results)
    }
}