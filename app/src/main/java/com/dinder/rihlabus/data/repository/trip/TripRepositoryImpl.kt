package com.dinder.rihlabus.data.repository.trip

import com.dinder.rihlabus.common.Constants
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Trip
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TripRepositoryImpl @Inject constructor(private val ioDispatcher: CoroutineDispatcher) :
    TripRepository {
    private val _ref = Firebase.firestore.collection(Constants.FireStoreCollection.TRIPS)
    override suspend fun addTrip(trip: Trip): Flow<Result<Boolean>> = callbackFlow {
        trySend(Result.Loading)
        withContext(ioDispatcher) {
            _ref.add(trip.toJson()).addOnSuccessListener {
                trySend(Result.Success(true))
            }
                .addOnFailureListener {
                    trySend(Result.Error("Failed to add trip"))
                }
        }
        awaitClose {
        }
    }
}
