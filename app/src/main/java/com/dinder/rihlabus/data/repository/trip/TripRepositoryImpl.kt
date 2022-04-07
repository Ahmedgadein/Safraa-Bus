package com.dinder.rihlabus.data.repository.trip

import android.util.Log
import com.dinder.rihlabus.common.Constants
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Trip
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ExperimentalCoroutinesApi
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
                    cancel()
                }
        }
        awaitClose()
    }

    override suspend fun getCurrentTrips(
        company: String,
        location: String
    ): Flow<Result<List<Trip>>> = callbackFlow {
        trySend(Result.Loading)
        _ref.whereEqualTo("from", location).whereEqualTo("company", company).get()
            .addOnSuccessListener { snapshot ->
                val results = snapshot.documents.map { Trip.fromJson(it.data!!) }
                trySend(Result.Success(results))
            }
            .addOnFailureListener {
                Log.i("CurrentTrips", "Error: $it")
                trySend(Result.Error(it.toString()))
            }
        awaitClose()
    }

    override suspend fun getTrip(id: Long): Flow<Result<Trip>> = callbackFlow {
        trySend(Result.Loading)
        _ref.whereEqualTo("id", id).limit(1).get()
            .addOnSuccessListener {
                trySend(Result.Success(Trip.fromJson(it.documents.first().data!!)))
            }
            .addOnFailureListener {
                trySend(Result.Error(it.toString()))
            }
        awaitClose()
    }
}
