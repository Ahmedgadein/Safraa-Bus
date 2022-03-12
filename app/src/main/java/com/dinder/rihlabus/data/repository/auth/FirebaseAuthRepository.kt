package com.dinder.rihlabus.data.repository.auth

import android.util.Log
import com.dinder.rihlabus.common.Constants
import com.dinder.rihlabus.data.model.Company
import com.google.firebase.auth.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


class FirebaseAuthRepository @Inject constructor(private val ioDispatcher: CoroutineDispatcher) :
    AuthRepository {
    private val _auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _ref = Firebase.firestore.collection(Constants.FireStoreCollection.USERS)

    private val _state: MutableStateFlow<AuthRepoState> = MutableStateFlow(AuthRepoState())
    override val state: StateFlow<AuthRepoState> = _state

    init {
        CoroutineScope(ioDispatcher).launch {
            val logged = _auth.currentUser != null
            val registered = _ref.document(_auth.uid.toString()).get().isSuccessful

            _state.update {
                it.copy(isLoggedIn = logged, isRegistered = registered)
            }
        }
    }

    override suspend fun login(credential: AuthCredential) {
        Log.i("Ahmed", "login: Logging in")
        CoroutineScope(ioDispatcher).launch {
            _auth.signInWithCredential(credential).addOnCompleteListener {
                if (it.isSuccessful)
                    _state.update {
                        it.copy(isLoggedIn = true)
                    }
            }
        }
    }

    override suspend fun register(
        credential: AuthCredential,
        name: String,
        company: Company
    ) {
        TODO("Not yet implemented")
    }
}