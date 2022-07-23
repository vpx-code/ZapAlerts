package com.xvlaze.zapalerts.model

import kotlinx.serialization.Serializable

@Serializable
data class Interest(
    var name: String,
    var frequency: Int,
    var language: Int,
    var country: Int,
    var lastUpdate: Long,
    var type: Int
)