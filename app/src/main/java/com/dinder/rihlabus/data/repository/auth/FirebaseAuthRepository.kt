package com.dinder.rihlabus.data.repository.auth

import com.dinder.rihlabus.common.Constants
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
class FirebaseAuthRepository @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val firebaseAuth: FirebaseAuth
) :
    AuthRepository {
    private val _ref = Firebase.firestore.collection(Constants.FireStoreCollection.USERS)
    init {
//        firebaseAuth.signOut()
    }
    override suspend fun isLoggedIn(): Flow<Result<Boolean>> = flow {
        emit(Result.Success(firebaseAuth.currentUser != null))
    }

    override suspend fun isRegistered(phoneNumber: String) = callbackFlow {
        withContext(CoroutineScope(ioDispatcher).coroutineContext) {
            trySend(Result.Loading)
            _ref.whereEqualTo("phoneNumber", phoneNumber).get()
                .addOnSuccessListener {
                    trySend(Result.Success(!it.isEmpty))
                }
                .addOnFailureListener {
                    trySend(Result.Error("Failed to check registration"))
                }
        }
        awaitClose {
        }
    }

    override suspend fun login(
        credential: AuthCredential,
        phoneNumber: String
    ): Flow<Result<Boolean>> = callbackFlow {
        withContext(CoroutineScope(ioDispatcher).coroutineContext) {
            trySend(Result.Loading)
            firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener {
                    trySend(Result.Success(true))
                }
                .addOnFailureListener {
                    trySend(Result.Error("Login Failed"))
                }
        }
        awaitClose {
        }
    }

    override suspend fun register(
        user: User
    ): Flow<Result<Boolean>> = callbackFlow {
        trySend(Result.Loading)
        withContext(CoroutineScope(ioDispatcher).coroutineContext) {
            isLoggedIn().collect {
                when (it) {
                    Result.Loading -> {}
                    is Result.Error -> {}
                    is Result.Success -> {
                        if (!it.value) {
                            trySend(Result.Error("Registration failed"))
                            return@collect
                        } else {
                            val id = firebaseAuth.currentUser?.uid!!
                            _ref.document(id).set(user.copy(id = id).toJson())
                                .addOnSuccessListener {
                                    trySend(Result.Success(true))
                                }
                                .addOnFailureListener {
                                    trySend(Result.Error("Registration failed"))
                                }
                        }
                    }
                }
            }
        }
        awaitClose {
        }
    }
}
