package com.dinder.rihlabus.data.repository.auth

import com.dinder.rihlabus.common.FireStoreCollection
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Company
import com.google.firebase.auth.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

enum class AuthError {
    unregistered,
    loginError
}

class FirebaseAuthRepository @Inject constructor() : AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val ref = Firebase.firestore.collection(FireStoreCollection.USERS)

    override val isLoggedIn: Boolean
        get() = auth.currentUser != null

    override val isRegistered: Boolean
        get() {
            val query = ref.whereEqualTo("id", auth.currentUser?.uid).get()
            return if (query.isSuccessful) query.result.documents.isNotEmpty() else false
        }

    override suspend fun login(credential: AuthCredential): Result<Boolean, AuthError> {
        lateinit var signInResult : Result<Boolean, AuthError>
        if (!isRegistered)
            return Result.Error(AuthError.unregistered)

        auth.signInWithCredential(credential).addOnCompleteListener {
            signInResult = if (it.isSuccessful) {
                Result.Success(true)
            } else {
                Result.Error(AuthError.loginError)
            }
        }

        return signInResult
    }

    override suspend fun register(
        credential: AuthCredential,
        name: String,
        company: Company
    ): Result<Boolean, AuthError> {
        TODO("Not yet implemented")
    }
}