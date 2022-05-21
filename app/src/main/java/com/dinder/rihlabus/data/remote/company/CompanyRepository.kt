package com.dinder.rihlabus.data.remote.company

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Company
import kotlinx.coroutines.flow.Flow

interface CompanyRepository {
    suspend fun upsert(company: Company): Flow<Result<Company>>
    suspend fun getCompanies(): Flow<Result<List<Company>>>
}
