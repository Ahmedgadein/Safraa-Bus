package com.dinder.rihlabus.data.repository.company

import com.dinder.rihlabus.common.Constants
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Company
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
class CompanyRepositoryImpl @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher
) : CompanyRepository {
    private val _ref = Firebase.firestore.collection(Constants.FireStoreCollection.COMPANIES)

    override suspend fun upsert(name: String): Flow<Result<Company>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo("name", name).get()
                .addOnSuccessListener { it ->
                    if (it.documents.isNullOrEmpty()) {
                        _ref.add(
                            mapOf(
                                "name" to name
                            )
                        ).addOnSuccessListener { addedCompanyReference ->
                            _ref.document(addedCompanyReference.id).get()
                                .addOnSuccessListener { success ->
                                    trySend(Result.Success(Company.fromJson(success.data!!)))
                                }
                        }
                            .addOnFailureListener {
                                trySend(Result.Error("Failed to find company"))
                            }
                    } else {
                        trySend(Result.Success(Company.fromJson(it.documents[0].data!!)))
                    }
                }
                .addOnFailureListener {
                    trySend(Result.Error("Failed to find company"))
                }
        }
        awaitClose()
    }
}
