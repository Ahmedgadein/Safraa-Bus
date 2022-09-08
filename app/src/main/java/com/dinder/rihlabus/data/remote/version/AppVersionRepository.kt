package com.dinder.rihlabus.data.remote.version

import com.dinder.rihlabus.common.Result
import com.dinder.rihlabus.data.model.UpdateApp
import kotlinx.coroutines.flow.Flow

interface AppVersionRepository {
    fun getAppVersion(): Flow<Result<UpdateApp>>
}
