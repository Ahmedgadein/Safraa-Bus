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
class FirebaseAuthRepository @Inject constructor(private val ioDispatcher: CoroutineDispatcher) :
    AuthRepository {
    private val _auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _ref = Firebase.firestore.collection(Constants.FireStoreCollection.USERS)

    private val _state: MutableStateFlow<AuthRepoState> = MutableStateFlow(AuthRepoState())
    override val state: StateFlow<AuthRepoState> = _state

    init {
//        _auth.signOut()
        _state.update { state ->
            state.copy(isLoggedIn = _auth.currentUser != null)
        }
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
            _auth.signInWithCredential(credential)
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
        withContext(CoroutineScope(ioDispatcher).coroutineContext) {
            trySend(Result.Loading)
            val id = _auth.currentUser?.uid!!
            _ref.document(id).set(user.copy(id = id).toJson())
                .addOnSuccessListener {
                    trySend(Result.Success(true))
                }
                .addOnFailureListener {
                    trySend(Result.Error("Registration failure"))
                }
        }
        awaitClose {

        }
    }
}