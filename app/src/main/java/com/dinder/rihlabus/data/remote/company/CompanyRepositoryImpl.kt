package com.dinder.rihlabus.data.remote.company

import com.dinder.rihlabus.common.Collections
import com.dinder.rihlabus.common.Fields
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
    private val _ref = Firebase.firestore.collection(Collections.COMPANIES)

    override suspend fun upsert(company: Company): Flow<Result<Company>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo(Fields.NAME, company.name).get()
                .addOnSuccessListener { it ->
                    if (it.documents.isNullOrEmpty()) {
                        _ref.add(
                            company.toJson()
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

    override suspend fun getCompanies(): Flow<Result<List<Company>>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.get()
                .addOnSuccessListener {
                    val companies = it.documents.map { json -> Company.fromJson(json.data!!) }
                    trySend(Result.Success(companies))
                }
                .addOnFailureListener {
                    trySend(Result.Error("Failed To Load Companies"))
                }
        }
        awaitClose()
    }
}
