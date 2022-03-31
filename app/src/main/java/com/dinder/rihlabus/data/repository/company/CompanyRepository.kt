package com.dinder.rihlabus.data.repository.company
import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.Company
import kotlinx.coroutines.flow.Flow

interface CompanyRepository {
    suspend fun upsert(name: String): Flow<Result<Company>>
}
