package com.xvlaze.zapalerts.repository

import com.xvlaze.zapalerts.model.InterestCloudObject

interface IDatabase {
    fun getAll()
    fun isInterestUnique(name: String): Boolean
    fun saveInterest(interest: InterestCloudObject)
    fun editInterest(interest: InterestCloudObject)
    fun deleteInterest(interest: InterestCloudObject)
    fun getInterestByName(name: String)
}