package com.dinder.rihlabus.data.remote.trip

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.model.Destination
import com.dinder.rihlabus.data.model.Trip
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    suspend fun addTrip(trip: Trip): Flow<Result<Boolean>>

    suspend fun getCurrentTrips(
        company: Company,
        location: Destination
    ): Flow<Result<List<Trip>>>

    suspend fun getLastTrips(
        company: Company,
        location: Destination
    ): Flow<Result<List<Trip>>>

    suspend fun getTrip(id: Long): Flow<Result<Trip>>

    fun observeTrip(id: Long): Flow<Result<Trip>>

    suspend fun confirmPayment(
        tripId: Long,
        seatNumber: Int
    ): Flow<Result<Unit>>

    suspend fun disprovePayment(
        tripId: Long,
        seatNumber: Int
    ): Flow<Result<Unit>>

    suspend fun bookSeat(
        tripId: Long,
        seatNumber: Int,
        passenger: String?
    ): Flow<Result<Unit>>
}
