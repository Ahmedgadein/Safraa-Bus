package com.dinder.rihlabus.data.remote.trip

import android.util.Log
import com.dinder.rihlabus.common.Collections
import com.dinder.rihlabus.common.Fields
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.model.Destination
import com.dinder.rihlabus.data.model.Trip
import com.dinder.rihlabus.utils.ErrorMessages
import com.dinder.rihlabus.utils.SeatState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

@ExperimentalCoroutinesApi
class TripRepositoryImpl @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val errorMessages: ErrorMessages
) :
    TripRepository {
    private val _ref = Firebase.firestore.collection(Collections.TRIPS)
    override suspend fun addTrip(trip: Trip): Flow<Result<Boolean>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.add(trip.toJson()).addOnSuccessListener {
                trySend(Result.Success(true))
            }
                .addOnFailureListener {
                    trySend(Result.Error(errorMessages.failedToAddTrip))
                    cancel()
                }
        }
        awaitClose()
    }

    override suspend fun getCurrentTrips(
        company: Company,
        location: Destination
    ): Flow<Result<List<Trip>>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo(Fields.FROM, location.toJson())
                .whereEqualTo(Fields.COMPANY, company.toJson())
                .whereGreaterThan(Fields.DATE, Date())
                .orderBy("date", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { snapshot ->
                    val results = snapshot.documents.map { Trip.fromJson(it.data!!) }
                    trySend(Result.Success(results))
                }
                .addOnFailureListener {
                    Log.i("CurrentTrips", "Error: $it")
                    trySend(Result.Error(errorMessages.failedToLoadCurrentTrips))
                }
        }
        awaitClose()
    }

    override suspend fun getLastTrips(
        company: Company,
        location: Destination
    ): Flow<Result<List<Trip>>> =
        callbackFlow {
            withContext(ioDispatcher) {
                trySend(Result.Loading)
                _ref.whereEqualTo(Fields.FROM, location.toJson())
                    .whereEqualTo(Fields.COMPANY, company.toJson())
                    .whereLessThan(Fields.DATE, Date())
                    .orderBy(Fields.DATE, Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val results = snapshot.documents.map { Trip.fromJson(it.data!!) }
                        trySend(Result.Success(results))
                    }
                    .addOnFailureListener {
                        trySend(Result.Error(errorMessages.failedToLoadLastTrips))
                    }
            }
            awaitClose()
        }

    override suspend fun getTrip(id: Long): Flow<Result<Trip>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo(Fields.ID, id).limit(1).get()
                .addOnSuccessListener {
                    trySend(Result.Success(Trip.fromJson(it.documents.first().data!!)))
                }
                .addOnFailureListener {
                    trySend(Result.Error(errorMessages.failedToLoadTrip))
                }
        }
        awaitClose()
    }

    override fun observeTrip(id: Long): Flow<Result<Trip>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo(Fields.ID, id).addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(errorMessages.failedToLoadTrip))
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.documents.isNotEmpty()) {
                    val trip = Trip.fromJson(snapshot.documents.first().data!!)
                    trySend(Result.Success(trip))
                } else {
                    trySend(Result.Error(errorMessages.failedToLoadTrip))
                }
            }
        }
        awaitClose()
    }

    override suspend fun confirmPayment(tripId: Long, seatNumber: Int): Flow<Result<Unit>> =
        callbackFlow {
            withContext(ioDispatcher) {
                trySend(Result.Loading)
                _ref.whereEqualTo(Fields.ID, tripId).limit(1).get()
                    .addOnSuccessListener {
                        _ref.document(it.documents[0].id).set(
                            mapOf(
                                Fields.SEATS to
                                    mapOf(
                                        "$seatNumber" to mapOf(
                                            Fields.STATUS to SeatState.PAID
                                        )
                                    )
                            ),
                            SetOptions.merge()
                        )
                            .addOnSuccessListener {
                                trySend(Result.Success(Unit))
                            }
                            .addOnFailureListener {
                                trySend(Result.Error(errorMessages.couldntConfirmPayment))
                            }
                    }
                    .addOnFailureListener {
                        trySend(Result.Error(errorMessages.failedToLoadTrip))
                    }
            }
            awaitClose()
        }

    override suspend fun disprovePayment(tripId: Long, seatNumber: Int): Flow<Result<Unit>> =
        callbackFlow {
            withContext(ioDispatcher) {
                trySend(Result.Loading)
                _ref.whereEqualTo(Fields.ID, tripId).limit(1).get()
                    .addOnSuccessListener {
                        _ref.document(it.documents[0].id).set(
                            mapOf(
                                Fields.SEATS to
                                    mapOf(
                                        "$seatNumber" to mapOf(
                                            Fields.STATUS to SeatState.UNBOOKED
                                        )
                                    )
                            ),
                            SetOptions.merge()
                        )
                            .addOnSuccessListener {
                                trySend(Result.Success(Unit))
                            }
                            .addOnFailureListener {
                                trySend(Result.Error(errorMessages.couldntConfirmPayment))
                            }
                    }
                    .addOnFailureListener {
                        trySend(Result.Error(errorMessages.failedToLoadTrip))
                    }
            }
            awaitClose()
        }

    override suspend fun bookSeat(
        tripId: Long,
        seatNumber: Int,
        passenger: String?
    ): Flow<Result<Unit>> = callbackFlow {
        withContext(ioDispatcher) {
            trySend(Result.Loading)
            _ref.whereEqualTo(Fields.ID, tripId).limit(1).get()
                .addOnSuccessListener {
                    _ref.document(it.documents[0].id).set(
                        mapOf(
                            Fields.SEATS to
                                mapOf(
                                    "$seatNumber" to mapOf(
                                        Fields.STATUS to SeatState.PAID,
                                        Fields.PASSENGER to passenger
                                    )
                                )
                        ),
                        SetOptions.merge()
                    )
                        .addOnSuccessListener {
                            trySend(Result.Success(Unit))
                        }
                        .addOnFailureListener {
                            trySend(Result.Error(errorMessages.failedToLoadTrip))
                        }
                }
                .addOnFailureListener {
                    trySend(Result.Error(errorMessages.failedToLoadTrip))
                }
        }
        awaitClose()
    }
}
