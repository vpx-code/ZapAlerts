package com.xvlaze.zapalerts.model

import android.content.Context
import com.huawei.hms.searchkit.SearchKitInstance
import com.huawei.hms.searchkit.bean.*
import com.huawei.hms.searchkit.utils.Language
import com.huawei.hms.searchkit.utils.Region
import com.xvlaze.zapalerts.util.Constants
import com.xvlaze.zapalerts.util.LanguageUtils.getLanguageFromDropdownSelection
import com.xvlaze.zapalerts.util.LanguageUtils.getRegionFromDropdownSelection
import java.util.*

interface OnNewsSearchPerformedCallback {
    fun onNewsSearchResult(result: ArrayList<NewsItem>)
}

interface OnImageSearchPerformedCallback {
    fun onImageSearchResult(result: ArrayList<ImageItem>)
}

interface OnVideoSearchPerformedCallback {
    fun onVideoSearchResult(result: ArrayList<VideoItem>)
}

interface OnWebsiteSearchPerformedCallback {
    fun onWebsiteSearchResult(result: ArrayList<WebItem>)
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
        if (newsSearchResponse.getData().isNotEmpty()) {
            results = newsSearchResponse.getData() as ArrayList<NewsItem>
            results.apply {
                distinctBy { it.title }
                distinctBy { it.clickUrl }
                sortByDescending { it.publishTime }
            }
        }
        callback.onNewsSearchResult(results)
    }
}

object VideoSearcher : Searchable<OnVideoSearchPerformedCallback>() {
    override fun search(
        query: String,
        language: Int,
        country: Int,
        c: Context,
        callback: OnVideoSearchPerformedCallback
    ) {
        val videoSearchRequest = CommonSearchRequest()
        videoSearchRequest.setQ(query)
        videoSearchRequest.setLang(
            getLanguageFromDropdownSelection(language)
        )
        videoSearchRequest.setSregion(
            getRegionFromDropdownSelection(country)
        )
        videoSearchRequest.setPs(Constants.maxElements)
        videoSearchRequest.setPn(1)
        val searchKitInstance = SearchKitInstance.getInstance()
        val token = OAuthTokenProvider.getOAuthTokenFromSharedPrefs(c)
        SearchKitInstance.instance.setInstanceCredential(token)
        val videoSearchResponse = searchKitInstance.videoSearcher.search(videoSearchRequest)
        var results = arrayListOf<VideoItem>()
        if (videoSearchResponse.getData().isNotEmpty())
            results = videoSearchResponse.getData() as ArrayList<VideoItem>
        callback.onVideoSearchResult(results)
    }
}

object ImageSearcher : Searchable<OnImageSearchPerformedCallback>() {
    override fun search(
        query: String,
        language: Int,
        country: Int,
        c: Context,
        callback: OnImageSearchPerformedCallback
    ) {
        val imageSearchRequest = CommonSearchRequest()
        imageSearchRequest.setQ(query)
        imageSearchRequest.setLang(
            getLanguageFromDropdownSelection(language)
        )
        imageSearchRequest.setSregion(
            getRegionFromDropdownSelection(country)
        )
        imageSearchRequest.setPs(Constants.maxElements)
        imageSearchRequest.setPn(1)
        val searchKitInstance = SearchKitInstance.getInstance()
        val token = OAuthTokenProvider.getOAuthTokenFromSharedPrefs(c)
        SearchKitInstance.instance.setInstanceCredential(token)
        val webSearchResponse = searchKitInstance.imageSearcher.search(imageSearchRequest)
        var results = arrayListOf<ImageItem>()
        if (webSearchResponse.getData().isNotEmpty())
            results = webSearchResponse.getData() as ArrayList<ImageItem>
        callback.onImageSearchResult(results)
    }
}

object WebSearcher : Searchable<OnWebsiteSearchPerformedCallback>() {
    override fun search(
        query: String,
        language: Int,
        country: Int,
        c: Context,
        callback: OnWebsiteSearchPerformedCallback
    ) {
        val webSearchRequest =
            CommonSearchRequest() as WebSearchRequest // TODO: Revisar y, si se puede, juntar (ver interfaces)
        webSearchRequest.setQ(query)
        webSearchRequest.setLang(
            getLanguageFromDropdownSelection(language)
        )
        webSearchRequest.setSregion(
            getRegionFromDropdownSelection(country)
        )
        webSearchRequest.setPs(Constants.maxElements)
        webSearchRequest.setPn(1)
        val searchKitInstance = SearchKitInstance.getInstance()
        val token = OAuthTokenProvider.getOAuthTokenFromSharedPrefs(c)
        SearchKitInstance.instance.setInstanceCredential(token)
        val webSearchResponse = searchKitInstance.webSearcher.search(webSearchRequest)
        var results = arrayListOf<WebItem>()
        if (webSearchResponse.getData().isNotEmpty())
            results = webSearchResponse.getData() as ArrayList<WebItem>
        callback.onWebsiteSearchResult(results)
    }
}