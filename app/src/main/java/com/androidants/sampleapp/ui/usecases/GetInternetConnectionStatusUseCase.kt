package com.androidants.sampleapp.ui.usecases

import android.content.Context
import com.androidants.sampleapp.data.repository.MainRepository
import javax.inject.Inject

class GetInternetConnectionStatusUseCase @Inject constructor(
    private val repository: MainRepository
) {
    suspend fun invoke(context : Context) : Boolean {
        return repository.getInternetConnectionStatus(context)
    }
}