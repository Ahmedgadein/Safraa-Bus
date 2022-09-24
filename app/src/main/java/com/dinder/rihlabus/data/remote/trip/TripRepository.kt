package com.dinder.rihlabus.data.remote.trip

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.model.Destination
import com.dinder.rihlabus.data.model.Trip
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    suspend fun addTrip(trip: Trip): Flow<Result<Boolean>>

//    suspend fun getCurrentTrips(
//        company: Company,
//        location: Destination
//    ): Flow<Result<List<Trip>>>

    suspend fun getCurrentTrips(): Flow<Result<List<Trip>>>

    suspend fun getLastTrips(
        company: Company,
        location: Destination
    ): Flow<Result<List<Trip>>>

    suspend fun getTrip(id: String): Flow<Result<Trip>>

    fun observeTrip(id: String): Flow<Result<Trip>>

    suspend fun confirmPayment(
        tripId: String,
        passengerName: String
    ): Flow<Result<Unit>>

    suspend fun disprovePayment(
        tripId: String,
        passengerName: String
    ): Flow<Result<Unit>>

    suspend fun bookSeat(
        tripId: String,
        seatNumber: Int,
        passenger: String?
    ): Flow<Result<Unit>>
}
