package com.dinder.rihlabus.data.repository.location

import com.dinder.rihlabus.common.Constants
import com.dinder.rihlabus.common.Result
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ExperimentalCoroutinesApi
class CompanyLocationRepositoryImpl @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher
) : CompanyLocationRepository {
    private val _ref = Firebase.firestore.collection(Constants.FireStoreCollection.LOCATIONS)

    override suspend fun upsert(location: String, company: String): Flow<Result<Boolean>> =
        callbackFlow {
            withContext(ioDispatcher) {
                trySend(Result.Loading)
                _ref.whereEqualTo("company", company)
                    .whereEqualTo("location", location).get()
                    .addOnSuccessListener {
                        if (it.isEmpty) {
                            _ref.add(
                                mapOf(
                                    "location" to location,
                                    "company" to company,
                                    "isActive" to true
                                )
                            ).addOnSuccessListener {
                                trySend(Result.Success(true))
                            }
                                .addOnFailureListener {
                                    trySend(Result.Error("Failed to add company"))
                                }
                        } else {
                            trySend(Result.Success(true))
                        }
                    }
                    .addOnFailureListener {
                        trySend(Result.Error("Failed to add company"))
                    }
            }
            awaitClose()
        }
}
