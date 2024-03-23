package com.androidants.sampleapp.data

import com.androidants.sampleapp.data.model.video.GetVideoResponse
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ApiCalls {

    @GET("/api/screens/syncScreenCode/{code}")
    suspend fun getVideos(@Path ("code") screenCode:String) : GetVideoResponse

}