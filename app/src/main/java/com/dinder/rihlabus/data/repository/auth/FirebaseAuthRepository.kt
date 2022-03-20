package com.dinder.rihlabus.data.repository.auth

import android.util.Log
import com.dinder.rihlabus.common.Constants
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.model.User
import com.dinder.rihlabus.data.model.toJson
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import javax.inject.Inject


class FirebaseAuthRepository @Inject constructor(private val ioDispatcher: CoroutineDispatcher) :
    AuthRepository {
    private val _auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _ref = Firebase.firestore.collection(Constants.FireStoreCollection.USERS)

    private val _state: MutableStateFlow<AuthRepoState> = MutableStateFlow(AuthRepoState())
    override val state: StateFlow<AuthRepoState> = _state

    init {
        _state.update { state ->
            Log.i(
                "Verification",
                "AuthRepo init: logged In=${_auth.currentUser != null}"
            )
            state.copy(isLoggedIn = _auth.currentUser != null)

        }
    }

    suspend fun isRegistered(phoneNumber: String) = callbackFlow<Result<Boolean>> {
        withContext(CoroutineScope(ioDispatcher).coroutineContext) {
            _ref.whereEqualTo("phoneNumber", "+249$phoneNumber").get()
                .addOnCompleteListener { registrationListener ->
                    if (registrationListener.isSuccessful) {
                        trySend(Result.Success(true))
                        Log.i(
                            "Verification",
                            "isRegistered Update: ${!registrationListener.result.documents.isNullOrEmpty()}"
                        )
                    }
                }
        }
        awaitClose {

        }
    }

    override suspend fun login(credential: AuthCredential, phoneNumber: String) {
        _state.update { state ->
            state.copy(loading = true)
        }
        Log.i("Loading", "Loading: ${_state.value.loading}")
        withContext(CoroutineScope(ioDispatcher).coroutineContext) {
            _ref.whereEqualTo("phoneNumber", phoneNumber).get()
                .addOnSuccessListener { document ->

                        Log.i(
                            "Verification",
                            "isRegistered Update: Number: $phoneNumber \n${!document.isEmpty}"
                        )

                    _auth.signInWithCredential(credential)
                        .addOnCompleteListener { loginListener ->
                            if (loginListener.isSuccessful) {
                                Log.i(
                                    "Verification",
                                    "isLoggedIn Update: ${loginListener.isSuccessful}"
                                )
                            }else{
                                Log.i(
                                    "Verification",
                                    "isLoggedIn Update: YOU FAILED MAN"
                                )
                            }
                            Log.i("Loading", "Loading: ${false}")

                            _state.update { state ->
                                state.copy(
                                    isLoggedIn = _auth.currentUser != null,
                                    isRegistered = !document.isEmpty,
                                    loading = false
                                )
                            }
                        }
                }
        }
    }

    override suspend fun register(
        user: User
    ): Flow<Result<Boolean>> = callbackFlow {
        trySend(Result.Loading)
        try {
            val id = _auth.currentUser?.uid!!
            val listener =
                _ref.document(id).set(user.copy(id = id).toJson()).addOnCompleteListener {
                    val result =
                        if (it.isSuccessful) Result.Success(true) else Result.Error("registration failed")
                    trySend(result)
                }
        } catch (e: Exception) {
            trySend(Result.Error("registration failed"))
        }
        awaitClose {

        }
    }
}