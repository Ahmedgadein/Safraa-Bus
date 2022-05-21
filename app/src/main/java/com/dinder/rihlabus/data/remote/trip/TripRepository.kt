package com.dinder.rihlabus.data.remote.trip

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.model.Destination
import com.dinder.rihlabus.data.model.Trip
import com.dinder.rihlabus.utils.SeatState
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

    suspend fun updateSeatState(
        tripId: Long,
        seatNumber: Int,
        state: SeatState
    ): Flow<Result<Boolean>>
}
