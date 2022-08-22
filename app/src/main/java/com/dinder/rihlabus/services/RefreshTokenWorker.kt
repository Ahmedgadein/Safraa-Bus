package com.dinder.rihlabus.services

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dinder.rihlabus.data.remote.auth.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@HiltWorker
class RefreshTokenWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    val repository: AuthRepository,
    val auth: FirebaseAuth
) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            val id = auth.uid.toString()

            return@withContext try {
                val token = FirebaseMessaging.getInstance().token.await()
                val tokenUpdated = repository.updateToken(id, token)
                if (!tokenUpdated) {
                    Result.Retry()
                } else {
                    Result.Success()
                }
            } catch (e: Exception) {
                Log.e("Token", e.toString())
                Result.Retry()
            }
        }
}
