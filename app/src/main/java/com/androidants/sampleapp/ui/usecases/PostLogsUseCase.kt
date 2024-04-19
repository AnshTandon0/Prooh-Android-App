package com.androidants.sampleapp.ui.usecases

import com.androidants.sampleapp.data.model.file.FileData
import com.androidants.sampleapp.data.model.log.LogReport
import com.androidants.sampleapp.data.repository.MainRepository
import retrofit2.Response
import javax.inject.Inject

class PostLogsUseCase @Inject constructor(
    private val repository: MainRepository
) {
    suspend fun invoke (screenId : String , logReport: LogReport) : Response<ArrayList<FileData>> {
        return repository.postLogs(screenId , logReport)
    }
}