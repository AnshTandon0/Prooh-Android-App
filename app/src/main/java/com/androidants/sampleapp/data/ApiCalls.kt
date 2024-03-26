package com.androidants.sampleapp.data

import com.androidants.sampleapp.data.model.log.LogReport
import com.androidants.sampleapp.data.model.video.GetVideoResponse
import com.androidants.sampleapp.data.model.video.MyScreenVideos
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiCalls {

    @GET("/api/screens/syncScreenCode/{code}")
    suspend fun getVideos(@Path ("code") screenCode:String) : Response<GetVideoResponse>

    @PUT("/api/screens/enterplaybacklogs/{screenId}")
    suspend fun postLogs(@Path ("screenId") screenId: String , @Body logReport: LogReport ) : ArrayList<MyScreenVideos>

}