package com.dinder.rihlabus.data.remote.version

import com.dinder.rihlabus.common.Collections
import com.dinder.rihlabus.common.Fields
import com.dinder.rihlabus.common.Result
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
class AppVersionRepositoryImpl @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val errorMessages: ErrorMessages
) : AppVersionRepository {

    private val _ref = Firebase.firestore.collection(Collections.CONSTANTS)

    override fun getAppVersion(): Flow<Result<String>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.document(Collections.APP_VERSION).get()
                .addOnSuccessListener {
                    trySend(Result.Success(it.data!![Fields.VERSION].toString()))
                }
                .addOnFailureListener {
                    trySend(Result.Error(errorMessages.failedToResolveAppVersion))
                }
        }
        awaitClose()
    }
}
