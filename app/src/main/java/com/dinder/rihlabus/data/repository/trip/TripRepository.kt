package com.dinder.rihlabus.data.repository.trip

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Trip
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    suspend fun addTrip(trip: Trip): Flow<Result<Boolean>>
}