package com.xvlaze.zapalerts.util

import com.huawei.hms.searchkit.utils.Language
import com.huawei.hms.searchkit.utils.Region
import java.util.*

object LanguageUtils {
    fun getPreferredLanguage(): Int {
        return when (Locale.getDefault().language) {
            "en" -> 0
            "es" -> 1
            "de" -> 2
            "it" -> 3
            "fr" -> 4
            "ar" -> 5
            "pl" -> 6
            "tr" -> 7
            "fi" -> 8
            "sv" -> 9
            "da" -> 10
            "nb" -> 11
            "pt" -> 12
            "cs" -> 13
            else -> 0
        }
    }

    fun getLanguageFromDropdownSelection(language: Int): Language {
        return when (language) {
            0 -> Language.ENGLISH
            1 -> Language.SPANISH
            2 -> Language.GERMAN
            3 -> Language.ITALIAN
            4 -> Language.FRENCH
            5 -> Language.ARABIC
            6 -> Language.POLISH
            7 -> Language.TURKISH
            8 -> Language.FINNISH
            9 -> Language.SWEDISH
            10 -> Language.DANISH
            11 -> Language.NORWEGIAN
            12 -> Language.PORTUGUESE
            13 -> Language.CZECH
            else -> Language.ENGLISH
        }
    }

    fun getRegionFromDropdownSelection(region: Int): Region {
        return when (region) {
            0 -> Region.WHOLEWORLD
            1 -> Region.CHILE
            2 -> Region.COLOMBIA
            3 -> Region.CZECH
            4 -> Region.DENMARK
            5 -> Region.EGYPT
            6 -> Region.FINLAND
            7 -> Region.FRANCE
            8 -> Region.GERMANY
            9 -> Region.INDIA
            10 -> Region.IRELAND
            11 -> Region.ITALY
            12 -> Region.MEXICO
            13 -> Region.NORWAY
            14 -> Region.PHILIPPINES
            15 -> Region.POLAND
            16 -> Region.PORTUGAL
            17 -> Region.SAUDIARABIA
            18 -> Region.SINGAPORE
            19 -> Region.SOUTHAFRICA
            20 -> Region.SPAIN
            21 -> Region.SWEDEN
            22 -> Region.TURKEY
            23 -> Region.UAE
            24 -> Region.UNITEDKINGDOM
            else -> Region.WHOLEWORLD
        }
    }
}