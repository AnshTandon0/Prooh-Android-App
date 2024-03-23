package com.androidants.sampleapp.ui.usecases

import com.androidants.sampleapp.data.model.video.GetVideoResponse
import com.androidants.sampleapp.data.repository.MainRepository
import javax.inject.Inject

class GetVideosUseCase @Inject constructor(
    private val repository: MainRepository
) {
    suspend fun invoke (screenCode:String) : GetVideoResponse {
        return repository.getVideoLinks(screenCode)
    }
}