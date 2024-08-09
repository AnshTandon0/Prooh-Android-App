package com.androidants.sampleapp.ui.usecases

import com.androidants.sampleapp.data.model.file.FileData
import com.androidants.sampleapp.data.model.log.CampaignLogs
import com.androidants.sampleapp.data.model.log.LogReportInput
import com.androidants.sampleapp.data.model.log.LogReportOutput
import com.androidants.sampleapp.data.repository.MainRepository
import retrofit2.Response
import javax.inject.Inject

class PostLogsUseCase @Inject constructor(
    private val repository: MainRepository
) {
    suspend fun invoke (logReportInput: LogReportInput) : Response<LogReportOutput> {
        return repository.postLogs(logReportInput)
    }
}