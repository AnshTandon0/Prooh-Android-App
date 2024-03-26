package com.androidants.sampleapp.ui.usecases

import com.androidants.sampleapp.data.model.log.LogReport
import com.androidants.sampleapp.data.model.video.MyScreenVideos
import com.androidants.sampleapp.data.repository.MainRepository
import javax.inject.Inject
import kotlin.math.log

class PostLogsUseCase @Inject constructor(
    private val repository: MainRepository
) {
    suspend fun invoke (screenId : String , logReport: LogReport) : ArrayList<MyScreenVideos> {
        return repository.postLogs(screenId , logReport)
    }
}