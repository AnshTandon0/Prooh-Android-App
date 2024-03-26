package com.androidants.sampleapp.ui.usecases

import com.androidants.sampleapp.data.model.video.GetVideoResponse
import com.androidants.sampleapp.data.repository.MainRepository
import retrofit2.Response
import javax.inject.Inject

class GetVideosUseCase @Inject constructor(
    private val repository: MainRepository
) {
    suspend fun invoke (screenCode:String) : Response<GetVideoResponse> {
        return repository.getVideoLinks(screenCode)
    }
}