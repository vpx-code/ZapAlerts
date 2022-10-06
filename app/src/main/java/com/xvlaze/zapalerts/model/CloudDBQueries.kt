package com.xvlaze.zapalerts.model

import android.util.Log
import com.huawei.agconnect.cloud.database.CloudDBZone
import com.huawei.agconnect.cloud.database.CloudDBZoneObjectList
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery
import com.huawei.hmf.tasks.Task

class CloudDBQueries(private val mCloudDBZone: CloudDBZone) : IDatabase {

    private var attempt = 1

    override fun getAll(callback: IOnGetAllSuccessCallback) {
        Log.d("ZAP_TAG", "Attempting to get saved interests from Database. Attempt $attempt")
        val result = mutableListOf<InterestCloudObject>()
        val queryTask = mCloudDBZone.executeQuery(
            CloudDBZoneQuery.where(InterestCloudObject::class.java)
                .equalTo("unionId", SharedPrefsProvider.getUserUid()),
            CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
        )
        queryTask
            .addOnSuccessListener { snapshot ->
                Log.d("ZAP_TAG", "Query succeeded!")
                val cursor: CloudDBZoneObjectList<InterestCloudObject> =
                    snapshot.snapshotObjects
                while (cursor.hasNext()) {
                    val baseFood = cursor.next()
                    result.add(baseFood)
                }
                Log.d("ZAP_TAG", "Results: ${result.size}")
                snapshot.release()
                if (result.isEmpty() && attempt < 3) {
                    Log.d("ZAP_TAG", "Result was empty.")
                    attempt++
                    getAll(callback)
                } else {
                    Log.d(
                        "ZAP_TAG",
                        "Result was populated or exceeded attempts! Calling callback..."
                    )
                    attempt = 0
                    callback.onSuccess(result)
                }
            }
            .addOnFailureListener {
                Log.d("ZAP_TAG", "Query failed. Reason: ${it.message}")
                Log.d("ZAP_TAG", "Query failed. Stack trace: ${it.stackTrace}")
            }
    }

    override fun isInterestUnique(
        name: String,
        interestList: MutableList<InterestCloudObject>
    ): Boolean {
        return interestList.none { it -> it.name.lowercase().trim() == name.lowercase().trim() }
    }

    override fun saveInterest(
        interest: InterestCloudObject,
        callback: IOnSaveInterestSuccessCallback
    ) {
        Log.d("ZAP_TAG", "Saving interest ${interest.name} to database...")
        getMaxId()
            .addOnSuccessListener { number ->
                var nextID = 1
                if (number != null) {
                    nextID = number.toInt() + 1
                }
                interest.id = nextID
                mCloudDBZone.executeUpsert(interest)
                    .addOnSuccessListener {
                        Log.d("ZAP_TAG", "Saved!")
                        callback.onSuccess(true)
                    }
                    .addOnFailureListener {
                        callback.onSuccess(false)
                    }
            }
            .addOnFailureListener {
                callback.onSuccess(false)
            }
    }

    private fun getMaxId(): Task<Number> {
        return mCloudDBZone.executeMaximumQuery(
            CloudDBZoneQuery
                .where(InterestCloudObject::class.java),
            "id",
            CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
        )
    }

    override fun editInterest(interest: InterestCloudObject, callback: IOnSaveInterestSuccessCallback) {
        mCloudDBZone.executeUpsert(interest)
            .addOnSuccessListener {
                callback.onSuccess(true)
            }
            .addOnFailureListener {
                callback.onSuccess(false)
            }
    }

    override fun deleteInterest(interest: InterestCloudObject) {
        mCloudDBZone.executeDelete(interest)
    }

    override fun getInterestByName(name: String, callback: IOnGetByNameSuccessCallback) {
        Log.d(
            "ZAP_TAG",
            "About to query. CloudDBZone is ${mCloudDBZone.cloudDBZoneConfig.cloudDBZoneName} and interest name is $name. UID is ${SharedPrefsProvider.getUserUid()}"
        )
        val queryTask2 = mCloudDBZone.executeQuery(
            CloudDBZoneQuery.where(InterestCloudObject::class.java)
                .equalTo("unionId", SharedPrefsProvider.getUserUid())
                .equalTo("name", name),
            CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_DEFAULT
        )

        val baseFoodListLocal = mutableListOf<InterestCloudObject>()
        queryTask2
            .addOnSuccessListener { snapshot ->
                Log.d("ZAP_TAG", "Query successful.")
                val cursor: CloudDBZoneObjectList<InterestCloudObject> =
                    snapshot.snapshotObjects

                try {
                    while (cursor.hasNext()) {
                        val baseFood = cursor.next()
                        baseFoodListLocal.add(baseFood)
                    }
                } catch (exception: Exception) {
                    Log.w("BaseFoodRepository", "getAllbaseFoods error: ${exception.message}")
                }

                if (baseFoodListLocal.isEmpty()) {
                    Log.d("ZAP_TAG", "Query was empty. Could not get interest. User UID: ${SharedPrefsProvider.getUserUid()}")
                }

                Log.d("ZAP_TAG", "Object to send to callback is ${baseFoodListLocal.first()}")
                snapshot.release()
                callback.onSuccess(baseFoodListLocal.first())
            }
            .addOnFailureListener {
                Log.d("ZAP_TAG", "Query failed. Reason: ${it.message}")
            }
    }
}

interface IOnGetAllSuccessCallback {
    fun onSuccess(res: MutableList<InterestCloudObject>)
}

interface IOnGetByNameSuccessCallback {
    fun onSuccess(res: InterestCloudObject)
}

interface IOnSaveInterestSuccessCallback {
    fun onSuccess(isCompleted: Boolean)
}
