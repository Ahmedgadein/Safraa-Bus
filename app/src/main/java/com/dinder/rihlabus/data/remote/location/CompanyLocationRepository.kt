package com.dinder.rihlabus.data.remote.location

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Company
import com.dinder.rihlabus.data.model.Destination
import kotlinx.coroutines.flow.Flow

interface CompanyLocationRepository {
    suspend fun upsert(location: Destination, company: Company): Flow<Result<Boolean>>
}
