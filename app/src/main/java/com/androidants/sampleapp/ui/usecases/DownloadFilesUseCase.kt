package com.androidants.sampleapp.ui.usecases

import android.content.Context
import com.androidants.sampleapp.data.model.VideoData
import com.androidants.sampleapp.data.repository.MainRepository
import javax.inject.Inject

class DownloadFilesUseCase @Inject constructor(
    private val repository: MainRepository
) {
    suspend fun invoke(context : Context, videoData: VideoData ) : VideoData {
        return repository.downloadVideo(context , videoData)
    }
}