package com.dinder.rihlabus.data.repository.user

import com.dinder.rihlabus.common.Constants
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.User
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
class UserRepositoryImpl @Inject constructor(private val ioDispatcher: CoroutineDispatcher) :
    UserRepository {
    private val _ref = Firebase.firestore.collection(Constants.FireStoreCollection.USERS)

    override fun get(id: String): Flow<Result<User>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo("id", id).limit(1).get()
                .addOnSuccessListener {
                    trySend(Result.Success(User.fromJson(it.documents[0].data!!)))
                }
                .addOnFailureListener {
                    trySend(Result.Error("Cannot find user"))
                }
        }
        awaitClose {

        }
    }
}