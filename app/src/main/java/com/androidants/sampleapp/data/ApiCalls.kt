package com.androidants.sampleapp.data

import com.androidants.sampleapp.data.model.file.GetFilesResponse
import com.androidants.sampleapp.data.model.log.LogReportInput
import com.androidants.sampleapp.data.model.log.LogReportOutput
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiCalls {

    @GET("/api/v1/screens/newSyncScreenCode")
    suspend fun getVideos(@Query ("syncCode") screenCode:String) : Response<GetFilesResponse>

    @POST("api/v1/analytics/addLogReport")
    suspend fun postLogs( @Body logReportInput: LogReportInput ) : Response<LogReportOutput>

}