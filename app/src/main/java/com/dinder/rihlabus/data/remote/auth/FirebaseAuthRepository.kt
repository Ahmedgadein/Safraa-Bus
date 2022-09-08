package com.dinder.rihlabus.data.remote.auth

import android.util.Log
import com.dinder.rihlabus.common.Collections
import com.dinder.rihlabus.common.Fields
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.User
import com.dinder.rihlabus.utils.ErrorMessages
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ExperimentalCoroutinesApi
class FirebaseAuthRepository @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val firebaseAuth: FirebaseAuth,
    private val errorMessages: ErrorMessages
) :
    AuthRepository {
    private val _ref = Firebase.firestore.collection(Collections.COMPANY_USERS)

    override suspend fun isLoggedIn(): Flow<Result<Boolean>> = flow {
        emit(Result.Success(firebaseAuth.currentUser != null))
    }

    override suspend fun isRegistered(phoneNumber: String) = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo(Fields.PHONE_NUMBER, phoneNumber).get()
                .addOnSuccessListener {
                    trySend(Result.Success(!it.isEmpty))
                }
                .addOnFailureListener {
                    trySend(Result.Error(errorMessages.failedToCheckRegistration))
                }
        }
        awaitClose()
    }

    override suspend fun login(
        credential: AuthCredential,
        phoneNumber: String
    ): Flow<Result<Boolean>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener {
                    trySend(Result.Success(true))
                }
                .addOnFailureListener {
                    trySend(Result.Error(errorMessages.loginFailed))
                }
        }
        awaitClose()
    }

    override suspend fun register(
        user: User
    ): Flow<Result<Boolean>> = callbackFlow {
        trySend(Result.Loading)
        withContext(ioDispatcher) {
            isLoggedIn().collect {
                when (it) {
                    Result.Loading -> {}
                    is Result.Error -> {}
                    is Result.Success -> {
                        if (!it.value) {
                            trySend(Result.Error(errorMessages.registrationFailed))
                            return@collect
                        } else {
                            val id = firebaseAuth.currentUser?.uid!!
                            FirebaseMessaging.getInstance().token
                                .addOnSuccessListener { token ->
                                    _ref.document(id)
                                        .set(user.copy(id = id, token = token).toJson())
                                        .addOnSuccessListener {
                                            trySend(Result.Success(true))
                                        }
                                        .addOnFailureListener {
                                            trySend(Result.Error(errorMessages.registrationFailed))
                                        }
                                }
                                .addOnFailureListener {
                                    trySend(Result.Error(errorMessages.registrationFailed))
                                }
                        }
                    }
                }
            }
        }
        awaitClose()
    }

    override suspend fun updateToken(id: String, token: String): Boolean {
        try {
            val user = _ref.whereEqualTo(Fields.ID, id).get().await()
            if (user.isEmpty) {
                return false
            }
            user.documents.first().reference.set(
                mapOf(
                    "token" to token
                ),
                SetOptions.merge()
            ).await()

            return true
        } catch (e: Exception) {
            Log.e("Token", e.toString())
            return false
        }
    }
}
