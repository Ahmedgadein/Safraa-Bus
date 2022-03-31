package com.dinder.rihlabus.data.repository.location

import com.dinder.rihlabus.common.Result
import kotlinx.coroutines.flow.Flow

interface CompanyLocationRepository {
    suspend fun upsert(location: String, company: String): Flow<Result<Boolean>>
}
