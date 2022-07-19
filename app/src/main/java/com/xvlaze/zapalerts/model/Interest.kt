package com.xvlaze.zapalerts.model

import com.xvlaze.zapalerts.util.Constants.AlertType
import com.xvlaze.zapalerts.util.Constants.InterestType
import kotlinx.serialization.Serializable

@Serializable
data class Interest(
    var name: String,
    var frequency: AlertType,
    var language: Int,
    var country: Int,
    var lastUpdate: Long,
    var type: InterestType
)