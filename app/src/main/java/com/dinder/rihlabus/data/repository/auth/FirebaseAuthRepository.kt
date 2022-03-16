package com.dinder.rihlabus.data.repository.auth

import android.util.Log
import com.dinder.rihlabus.common.Constants
import com.dinder.rihlabus.data.model.Company
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class FirebaseAuthRepository @Inject constructor(private val ioDispatcher: CoroutineDispatcher) :
    AuthRepository {
    private val _auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _ref = Firebase.firestore.collection(Constants.FireStoreCollection.USERS)

    private val _state: MutableStateFlow<AuthRepoState> = MutableStateFlow(AuthRepoState())
    override val state: StateFlow<AuthRepoState> = _state

    init {
        CoroutineScope(ioDispatcher).launch {
            _ref.whereEqualTo("id", _auth.currentUser?.uid).addSnapshotListener { value, error ->
                _state.update { state ->
                    Log.i(
                        "Verification",
                        "AuthRepo init: logged=${value != null} registered=${_auth.currentUser != null}"
                    )
                    state.copy(isRegistered = value != null, isLoggedIn = _auth.currentUser != null)

                }
            }
        }
    }

    override suspend fun login(credential: AuthCredential) {
        Log.i("Ahmed", "login: Logging in")
        withContext(CoroutineScope(ioDispatcher).coroutineContext) {
            _auth.signInWithCredential(credential).addOnCompleteListener {
                if (it.isSuccessful)
                    _state.update { state ->
                        state.copy(isLoggedIn = true)
                    }
            }
        }
    }

    override suspend fun register(
        credential: AuthCredential,
        name: String,
        phoneNumber: String,
        company: Company
    ) {
        withContext(CoroutineScope(ioDispatcher).coroutineContext) {
            _auth.signInWithCredential(credential).addOnCompleteListener {
                if (it.isSuccessful)
                    _state.update { state ->
                        state.copy(isLoggedIn = true)
                    }
            }
        }

        if (_state.value.isRegistered)
            return

        CoroutineScope(ioDispatcher).launch {
            _ref.document(_auth.currentUser?.uid!!).set(
                hashMapOf(
                    "id" to _auth.currentUser?.uid,
                    "phoneNumber" to phoneNumber,
                    "name" to name,
                    "company" to company
                )
            ).addOnCompleteListener {
                if (it.isSuccessful)
                    _state.update { state ->
                        state.copy(isRegistered = true)
                    }
            }
        }
    }
}