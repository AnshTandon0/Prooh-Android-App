package com.androidants.sampleapp.data

import com.androidants.sampleapp.data.model.file.FileData
import com.androidants.sampleapp.data.model.file.GetFilesResponse
import com.androidants.sampleapp.data.model.log.LogReport
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiCalls {

    @GET("/api/v1/screens/syncScreenCode")
    suspend fun getVideos(@Query ("syncCode") screenCode:String) : Response<GetFilesResponse>

    @PUT("/api/screens/enterplaybacklogs/{screenId}")
    suspend fun postLogs(@Path ("screenId") screenId: String , @Body logReport: LogReport ) : Response<ArrayList<FileData>>

}