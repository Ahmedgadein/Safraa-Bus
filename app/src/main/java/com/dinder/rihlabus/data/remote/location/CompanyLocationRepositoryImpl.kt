package com.dinder.rihlabus.data.remote.location

import com.dinder.rihlabus.common.Collections
import com.dinder.rihlabus.common.Fields
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.model.Destination
import com.dinder.rihlabus.utils.ErrorMessages
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
    private val ioDispatcher: CoroutineDispatcher,
    private val errorMessages: ErrorMessages
) : CompanyLocationRepository {
    private val _ref = Firebase.firestore.collection(Collections.COMPANY_LOCATIONS)

    override suspend fun upsert(location: Destination, company: Company): Flow<Result<Boolean>> =
        callbackFlow {
            withContext(ioDispatcher) {
                trySend(Result.Loading)
                _ref.whereEqualTo(Fields.COMPANY, company.toJson())
                    .whereEqualTo(Fields.LOCATION, location.toJson()).get()
                    .addOnSuccessListener {
                        if (it.isEmpty) {
                            _ref.add(
                                mapOf(
                                    "location" to location.toJson(),
                                    "company" to company.toJson(),
                                    "isActive" to true
                                )
                            ).addOnSuccessListener {
                                trySend(Result.Success(true))
                            }
                                .addOnFailureListener {
                                    trySend(Result.Error(errorMessages.failedToAddCompany))
                                }
                        } else {
                            trySend(Result.Success(true))
                        }
                    }
                    .addOnFailureListener {
                        trySend(Result.Error(errorMessages.failedToAddCompany))
                    }
            }
            awaitClose()
        }
}
