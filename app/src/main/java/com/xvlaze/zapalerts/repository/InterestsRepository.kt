package com.xvlaze.zapalerts.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.huawei.agconnect.cloud.database.CloudDBZone
import com.huawei.agconnect.cloud.database.CloudDBZoneObjectList
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery
import com.huawei.agconnect.cloud.database.CloudDBZoneSnapshot
import com.xvlaze.zapalerts.model.InterestCloudObject
import com.xvlaze.zapalerts.model.User

class InterestsRepository(val mCloudDBZone: CloudDBZone) : IDatabase {

    val interestsList = MutableLiveData<MutableList<InterestCloudObject>>()
    val interestToEdit = MutableLiveData<InterestCloudObject>()


    override fun getAll() {
        val queryTask = mCloudDBZone.executeQuery(
            CloudDBZoneQuery.where(InterestCloudObject::class.java)
                .equalTo("unionId", User.unionId),
            CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
        )

        queryTask
            .addOnSuccessListener { snapshot ->
                getAllBaseFoodsResultHandler(snapshot)
            }
            .addOnFailureListener {
                //mUiCallBack.updateUiOnError("Query book list from cloud failed")
            }
    }

    // TODO: Mirar si realmente funciona.
    override fun isInterestUnique(name: String): Boolean {
        val queryTask = mCloudDBZone.executeQuery(
            CloudDBZoneQuery.where(InterestCloudObject::class.java)
                .equalTo("unionId", User.unionId)
                .equalTo("name", name),
            CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
        )
        val baseFoodListLocal = mutableListOf<InterestCloudObject>()

        queryTask
            .addOnSuccessListener { snapshot ->
                val baseFoodCursor: CloudDBZoneObjectList<InterestCloudObject> =
                    snapshot.snapshotObjects
                try {
                    while (baseFoodCursor.hasNext()) {
                        val baseFood = baseFoodCursor.next()
                        baseFoodListLocal.add(baseFood)
                    }
                } catch (exception: Exception) {
                    Log.w("BaseFoodRepository", "getAllbaseFoods error: ${exception.message}")
                }

                snapshot.release()
            }
            .addOnFailureListener {
            }

        return baseFoodListLocal.isEmpty()
    }

    private fun getAllBaseFoodsResultHandler(snapshot: CloudDBZoneSnapshot<InterestCloudObject>) {
        val baseFoodCursor: CloudDBZoneObjectList<InterestCloudObject> = snapshot.snapshotObjects
        val baseFoodListLocal = mutableListOf<InterestCloudObject>()

        try {
            while (baseFoodCursor.hasNext()) {
                val baseFood = baseFoodCursor.next()
                baseFoodListLocal.add(baseFood)
            }
        } catch (exception: Exception) {
            Log.w("BaseFoodRepository", "getAllbaseFoods error: ${exception.message}")
        }

        snapshot.release()

        interestsList.postValue(baseFoodListLocal)
    }

    override fun saveInterest(interest: InterestCloudObject) {
        val getMaxId = mCloudDBZone.executeMaximumQuery(
            CloudDBZoneQuery
                .where(InterestCloudObject::class.java),
            "id",
            CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
        )
        getMaxId
            .addOnSuccessListener { number ->
                var nextID = 1
                if (number != null) {
                    nextID = number.toInt() + 1
                }

                interest.id = nextID
                val upsertTask = mCloudDBZone.executeUpsert(interest)
                upsertTask
                    .addOnSuccessListener { cloudDBZoneResult ->
                        Log.d("ZAP_TAG", "Successfully upserted")
                    }
                    .addOnFailureListener {
                        Log.d("ZAP_TAG", it.toString())
                    }
            }.addOnFailureListener {
                Log.w("ZAP_TAG", "Maximum query is failed: " + Log.getStackTraceString(it))
            }
    }

    override fun editInterest(interest: InterestCloudObject) {
        saveInterest(interest)
    }

    override fun deleteInterest(interest: InterestCloudObject) {
        mCloudDBZone.executeDelete(interest)
    }


    // TODO: Hacer con LiveData. No lo hace bien.
    override fun getInterestByName(name: String) {
        val queryTask2 = mCloudDBZone.executeQuery(
            CloudDBZoneQuery.where(InterestCloudObject::class.java)
                .equalTo("unionId", User.unionId)
                .equalTo("name", name),
            CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY
        )

        val baseFoodListLocal = mutableListOf<InterestCloudObject>()
        queryTask2
            .addOnSuccessListener { snapshot ->
                val baseFoodCursor: CloudDBZoneObjectList<InterestCloudObject> =
                    snapshot.snapshotObjects

                try {
                    while (baseFoodCursor.hasNext()) {
                        val baseFood = baseFoodCursor.next()
                        baseFoodListLocal.add(baseFood)
                    }
                    interestToEdit.postValue(baseFoodListLocal.first())
                } catch (exception: Exception) {
                    Log.w("BaseFoodRepository", "getAllbaseFoods error: ${exception.message}")
                }
                snapshot.release()
            }
    }
}