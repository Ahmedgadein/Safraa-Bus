package com.dinder.rihlabus.data.remote.user

import com.dinder.rihlabus.common.Collections
import com.dinder.rihlabus.common.Fields
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.local.UserDao
import com.dinder.rihlabus.data.model.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

@ExperimentalCoroutinesApi
class UserRepositoryImpl @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val dao: UserDao
) :
    UserRepository {

    private val _ref = Firebase.firestore.collection(Collections.COMPANY_USERS)

    override val user = channelFlow {
        withContext(ioDispatcher) {
            val user = dao.getUser()
            send(user)
        }
    }

    override fun get(id: String): Flow<Result<User>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo(
                Fields.ID,
                id
            ).limit(1).get()
                .addOnSuccessListener {
                    trySend(Result.Success(User.fromJson(it.documents[0].data!!)))
                }
                .addOnFailureListener {
                    trySend(Result.Error("Cannot find user"))
                }
        }
        awaitClose()
    }

    override fun add(user: User) {
        CoroutineScope(ioDispatcher).launch {
            dao.insert(user)
        }
    }
}
