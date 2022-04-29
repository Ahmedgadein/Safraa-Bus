package com.dinder.rihlabus.data.remote.repository.trip

import android.util.Log
import com.dinder.rihlabus.common.Constants
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Trip
import com.dinder.rihlabus.utils.SeatState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
class TripRepositoryImpl @Inject constructor(private val ioDispatcher: CoroutineDispatcher) :
    TripRepository {
    private val _ref = Firebase.firestore.collection(Constants.FireStoreCollection.TRIPS)
    override suspend fun addTrip(trip: Trip): Flow<Result<Boolean>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
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
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo("from", location)
                .whereEqualTo("company", company)
                .whereGreaterThan("date", Date())
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { snapshot ->
                    val results = snapshot.documents.map { Trip.fromJson(it.data!!) }
                    trySend(Result.Success(results))
                }
                .addOnFailureListener {
                    Log.i("CurrentTrips", "Error: $it")
                    trySend(Result.Error(it.toString()))
                }
        }
        awaitClose()
    }

    override suspend fun getLastTrips(company: String, location: String): Flow<Result<List<Trip>>> =
        callbackFlow {
            withContext(ioDispatcher) {
                trySend(Result.Loading)
                _ref.whereEqualTo("from", location)
                    .whereEqualTo("company", company)
                    .whereLessThan("date", Date())
                    .orderBy("date", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val results = snapshot.documents.map { Trip.fromJson(it.data!!) }
                        trySend(Result.Success(results))
                    }
                    .addOnFailureListener {
                        trySend(Result.Error(it.toString()))
                    }
            }
            awaitClose()
        }

    override suspend fun getTrip(id: Long): Flow<Result<Trip>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo("id", id).limit(1).get()
                .addOnSuccessListener {
                    trySend(Result.Success(Trip.fromJson(it.documents.first().data!!)))
                }
                .addOnFailureListener {
                    trySend(Result.Error(it.toString()))
                }
        }
        awaitClose()
    }

    override suspend fun updateSeatState(
        tripId: Long,
        seatNumber: Int,
        state: SeatState
    ): Flow<Result<Boolean>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo("id", tripId).limit(1).get()
                .addOnSuccessListener {
                    _ref.document(it.documents[0].id).set(
                        mapOf(
                            "seats" to
                                mapOf(
                                    "$seatNumber" to mapOf(
                                        "status" to state
                                    )
                                )
                        ),
                        SetOptions.merge()
                    )
                        .addOnSuccessListener {
                            Log.i("UpdateSeatState", "updateSeatState status: Successful")
                            trySend(Result.Success(true))
                        }
                        .addOnFailureListener {
                            Log.i("UpdateSeatState", "updateSeatState status: Failure")
                            trySend(Result.Error("Failed to update seat state"))
                        }
                }
                .addOnFailureListener {
                    trySend(Result.Error("Failed to find trip"))
                }
        }
        awaitClose()
    }
}
